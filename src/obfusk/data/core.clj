; --                                                              {{{1

;;      File        : obfusk/data/core.clj
;;      Maintainer  : Felix C. Stegerman <flx@obfusk.net>
;;      Date        : 2013-01-13
;;
;;      Copyright   : Copyright (C) 2013  Felix C. Stegerman
;;      Licence     : GPLv2 or EPLv1

; --                                                              }}}1

;; [clj-]obfusk-data - data validation combinator library for clojure

(ns obfusk.data.core
  (require [clojure.set :as _s]) )

; --

(defn blank? [x] (if (or (string? x) (coll? x)) (empty? x) false))

(declare validate)

(defn -error [st & msg]
  (update-in st [:errors] #(conj % (apply str msg))) )

; --

(defn data                                                      ; {{{1
  "A data validator.  ..."
  [options & fields]
  (let [ { :keys [other-fields isa] } options
         st { :errors [], :processed #{} } ]
    (fn [x]
      (if (and (seq isa) (some #(validate % x) isa))
        (-error st "[data] has failed isa")                     ; TODO
        (let [ st' (reduce (fn [st' f] (f x st')) st fields)
               ks  (set (keys x))
               pks (:processed st')
               eks (_s/difference ks pks) ]
          (cond
            (and (not other-fields) (seq eks))
              (-error st' "[data] has extraneous fields")
            (and (ifn? other-fields) (not (every? other-fields eks)))
              (-error st' "[data] has invalid other fields")
            :else st' ))))))
                                                                ; }}}1

(defmacro defdata
  [name options & fields]
  `(def ~name (data ~options ~@fields)) )

; --

(defn -validate-field                                           ; {{{1
  [name predicates options x st]
  (let [ field  (get x name)
         st'    (update-in st [:processed] #(conj % name))
         err    #(-error st' (or (:message options)
                  (apply str %&) ))
                      [ optional o-nil  blank  isa o-if o-if-not]
         (map options [:optional  :nil :blank :isa  :if  :if-not]) ]
    (cond
      (or (and o-if (not (o-if x))) (and o-if-not (o-if-not x)))
        st'
      (not (contains? x name))
        (if optional st' (err "[field] not found: " name))
      (nil? field)
        (if (or o-nil optional) st' (err "[field] is nil: " name))
      (and (blank? field) (not (or blank optional)))
        (err "[field] is blank: " name)
      (not-every? #(% field) predicates)
        (err "[field] has failed predicates: " name)
      (and (seq isa) (some #(validate % field) isa))
        (err "[field] has failed isa: " name)                   ; TODO
      :else st' )))
                                                                ; }}}1

(defn field
  "A data field.  Predicates are functions that are invoked with the
  field's value, and must all return true.  ..."
  [names predicates & [options]]
  (let [ o (or options {})
         f #(-validate-field %1 predicates o %2 %3)
         n (if (sequential? names) names [names]) ]
    (fn [x st] (reduce (fn [s name] (f name x s)) st n)) ))

; --

(defn union
  "Union of data fields.  ..."
  [k & body]
  (let [ b (into {} (map #(vector (first %) (rest %)) body)) ]
    (fn [x st]
      (let [ fields   ((k x) b)
             fields'  (conj fields (field k [keyword?])) ]
        (reduce (fn [s field] (field x s)) st fields') ))))

(defmacro defunion
  [name key options & body]
  `(def ~name (data ~options (union ~key ~@body))) )

; --

(defn validate
  "Validate; returns nil if valid, errors otherwise."
  [f x] (seq (:errors (f x))) )

(defn valid?
  "Validate; returns true/false."
  [f x] (nil? (validate f x)) )

; vim: set tw=70 sw=2 sts=2 et fdm=marker :
