[]: {{{1

    File        : README.md
    Maintainer  : Felix C. Stegerman <flx@obfusk.net>
    Date        : 2013-02-11

    Copyright   : Copyright (C) 2013  Felix C. Stegerman
    Version     : 0.0.2-SNAPSHOT

[]: }}}1

## Description
[]: {{{1

  [clj-]obfusk-data - data validation combinator library for clojure

  ...

```clojure
(defdata address {}
  (field [:street :number :postal-code :town] [string?])
  (field :country [string?] { :optional true }) )

(defn email? [x] (re-matches #".*@.*\.[a-z]+" x))

(defdata person {}
  (field [:first-name :last-name :phone-number] [string?])
  (field :email [string? email?])
  (field :address [] { :isa [address] }) )

(defunion tree :type {}
  [ :empty ]
  [ :leaf (field :value []) ]
  [ :node (field [:left :right] [] { :isa [#'tree] }) ] )

(valid? tree
  { :type :node
    :left { :type :empty }
    :right { :type :leaf, :value "spam!" } } )
; => true
```

[]: }}}1

## Specs & Docs
[]: {{{1

    $ rake spec
    $ rake docs

[]: }}}1

## TODO
[]: {{{1

  * write more specs
  * write more docs
  * show isa errors
  * ...

[]: }}}1

## License
[]: {{{1

  GPLv2 [1] or EPLv1 [2].

[]: }}}1

## References
[]: {{{1

  [1] GNU General Public License, version 2
  --- http://www.opensource.org/licenses/GPL-2.0

  [2] Eclipse Public License, version 1
  --- http://www.opensource.org/licenses/EPL-1.0

[]: }}}1

[]: ! ( vim: set tw=70 sw=2 sts=2 et fdm=marker : )
