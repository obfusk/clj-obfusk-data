; --                                                              {{{1

;;      File        : loki/util/data/core.clj
;;      Maintainer  : Felix C. Stegerman <flx@obfusk.net>
;;      Date        : 2013-01-12
;;
;;      Copyright   : Copyright (C) 2013  Felix C. Stegerman
;;      Licence     : GPLv2 or EPLv1

; --                                                              }}}1

;; ...

(ns loki.util.data.core)

(defn data
  "..."
  [opts & body]
    (fn f
      ([x] (f x nil))
      ([x context]
        :...
)))

(defmacro defdata [name & body]
  `(def ~name (data ~@body)) )

(defn field
  "..."
  ([names predicates] (field names predicates {}))
  ([names predicates options]
    (fn [x opts]
      :...
)))

(defn valid?
  "..."
  [f x] (= :... (f x))
)

; vim: set tw=70 sw=2 sts=2 et fdm=marker :
