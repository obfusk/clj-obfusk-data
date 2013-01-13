; --                                                              {{{1

;;      File        : loki/util/data/core_spec.clj
;;      Maintainer  : Felix C. Stegerman <flx@obfusk.net>
;;      Date        : 2013-01-12
;;
;;      Copyright   : Copyright (C) 2013  Felix C. Stegerman
;;      Licence     : GPLv2 or EPLv1

; --                                                              }}}1

;; specs for loki.util.data

(ns loki.util.data.core-spec
  (:use speclj.core loki.util.data.core) )

; --

(defn email? [x] (re-matches #".*@.*\.[a-z]+" x))
(defn url?   [x] (re-matches #"https?://.*"   x))

(defn object-id? [x]
  (and (string? x) (= 16 (count x))
    (every? #(re-matches #"[a-zA-Z0-9]" x)) ))

(defn id-seq? [x] (and (sequential? x) (every? object-id? x)))
(defn id-map? [m]
  (and (map? m) (every? #(and (string? %1) (object-id? %2)) m)) )

; --

(defdata foo { :other-fields #(re-matches #"data-.*" (name %)) })

(defunion tree :type {}
  [:empty]
  [:leaf (field :value []) ]
  [:node (field [:left :right] [] { :isa [#'tree] }) ] )

; --

(defdata address {}
  (field [:street :number :postal-code :town]
                        [string?])
  (field :country       [string?] { :optional true })
)

(defdata person {}
  (field [:first-name :last-name :phone-number]
                        [string?])
  (field :email         [string? email?])
  (field :address       [] { :isa [address] })
)

; --

(defdata collection {}                                          ; {{{1
  (field :_id           [object-id?])
  (field :app           [string?])
  (field :icon          [object-id?])
  (field :items         [id-seq?])
  (field :title         [string?] { :optional true })
)                                                               ; }}}1

(defdata item {}                                                ; {{{1
  (field :_id           [object-id?])
  (field :type          [string?])
  (field :icon          [object-id?]  { :nil true })
  (field :data          []            { :optional true })
  (field :title         [string?]     { :optional true })
  (field :url           [url?]        { :optional true })
  (field [:refs :files] [id-map?]     { :optional true })

  ; (not= (contains? x :url)
  ;       (contains? (get x :files {}) :url))
)                                                               ; }}}1

(defdata item-files {}
  (field :url           [url?] { :optional true })
)

(defdata image-item { :isa [item] }
  (field :icon          [nil?])
  (field :data          [nil?] { :optional true })
  (field :files         { :isa [item-files] })
)

; --

(describe "loki.util.data.core"

  (context "foo"                                                ; {{{1

    (it "valid empty foo"
      (should (valid? foo {})) )

    (it "valid foo"
      (should (valid? foo { :data-bar 37 })) )

    (it "invalid foo"
      (should-not (valid? foo { :baz 42 })) )

  )                                                             ; }}}1

  (context "tree"                                               ; {{{1

    (it "valid empty tree"
      (should (valid? tree { :type :empty })) )

    (it "invalid empty tree"
      (should-not (valid? tree { :type :empty, :foo "hi!" })) )

    (it "valid tree leaf"
      (should (valid? tree { :type :leaf, :value 3.14 })) )

    (it "invalid tree leaf"
      (should-not (valid? tree { :type :leaf })) )

    (it "valid tree node"
      (should (valid? tree
        { :type :node
          :left { :type :empty }
          :right { :type :leaf, :value "spam!" } })))

    (it "invalid tree node"
      (should-not (valid? tree
        { :type :node
          :left { :type :empty }
          :right nil })))

  )                                                             ; }}}1

  (context "address"                                            ; {{{1

    (it "valid address"
      (should (valid? address
        { :street "baker street" :number "221b" :town "london"
          :postal-code "i don't know" :country "uk" } )))

    (it "invalid address"
      (should-not (valid? address
        { :street "baker street" :number 404 } )))

  )                                                             ; }}}1

  ; ...
)

; --

(run-specs)

; vim: set tw=70 sw=2 sts=2 et fdm=marker :
