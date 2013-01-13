; --                                                              {{{1

;;      File        : loki/util/data/core.clj
;;      Maintainer  : Felix C. Stegerman <flx@obfusk.net>
;;      Date        : 2013-01-12
;;
;;      Copyright   : Copyright (C) 2013  Felix C. Stegerman
;;      Licence     : GPLv2 or EPLv1

; --                                                              }}}1

;; data validation

(ns loki.util.data.core
  (require [clojure.set :as _s]) )

; --

(defn -error [st & msg]
  (update-in st [:errors] #(conj % (apply str msg))) )

(defn -validate-fields
  :...)

(defn data                                                      ; {{{1
  "A data validator.  ..."
  [options & fields]
  (let [ { :keys [other-fields isa] } options
         st { :errors [], :processed {} } ]
    (fn f [x]
      (if (and (seq isa) (some #(validate % x) isa))
        (-error st "[data] has failed isa")                     ; TODO
        (let [ st' (-validate-fields x st fields)
               ks  (set (keys x))
               pks (set (:processed st'))
               eks (_s/difference pks ks) ]
          (cond
            (seq (:errors st'))
              st'
            (and (not other-fields) (seq eks))
              (-error st' "[data] has extraneous fields")
            (and (ifn? other-fields) (not (every? other-fields eks)))
              (-error st' "[data] has invalid other fields")
            :else st' ))))))
                                                                ; }}}1
(defmacro defdata
  [name options & fields]
  `(def ~name (data options ~@fields)) )

; --

(defn blank? [x]
  (if (or (string? x) (coll? x)) (empty? x) false) )

(declare validate)

(defn -validate-field                                           ; {{{1
  [name predicates options x st]
  (let [ field        (get x name)
         st'          (assoc-in st [:processed name] true)
         err          #(-error st' (or (:message options) (str &%)))
                      [ optional o-nil  blank  isa o-if o-if-not]
         (map options [:optional  :nil :blank :isa  :if  :if-not]) ]
    (cond
      (or (and o-if (not (o-if x))) (and o-if-not (o-if-not x)))
        st'
      (not (contains? x name))
        (if optional
          st'
          (err "[field] not found: " name) )
      (nil? field)
        (if (or o-nil optional)
          st'
          (err "[field] is nil: " name) )
      (and (blank? field) (not (or blank optional)))
        (err "[field] is blank: " name)
      (not-every? #(% field) predicates)
        (err "[field] has failed predicates: " name)
      (and (seq isa) (some #(validate % field) isa))
        (err "[field] has failed isa: " name)                   ; TODO
      :else st' )))))
                                                                ; }}}1

(defn field
  "A data field.  Predicates are functions that are invoked with the
  field's value, and must all return true.  ..."
  [names predicates & [options]]
  (fn [x st]
    (reduce
      (fn [s name] (-validate-field name predicates options x s))
      st
      (if (sequential? names) names [names]) )))

; --

(defn union
  "Union of data fields.  ..."
  [f & body]
  (let [ b (into {} (map #(vector (first %) (rest %)) body)) ]
    (fn [x st]
      (if-let [ t (f x) ]
        ((t b) x st)
        (-error st "[union] not of type " t) ))))

(defmacro defunion
  [name f options & fields]
  `(def ~name (data options (union ~f ~@fields))) )

; --

(defn validate
  "Validate; returns nil if valid, error information otherwise."
  [f x] (seq (:errors (f x))) )

(defn valid?
  "Validate; returns true/false."
  [f x] (nil? (validate f x)) )

; vim: set tw=70 sw=2 sts=2 et fdm=marker :
