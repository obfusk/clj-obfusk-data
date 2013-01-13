(defproject obfusk-data "0.0.2-SNAPSHOT"
  :description
    (str        "[clj-]obfusk-data - "
                "data validation combinator library for clojure" )
  :url          "https://github.com/obfusk/clj-obfusk-data"

  :licenses [ { :name "GPLv2", :distribution :repo
                :url "http://www.opensource.org/licenses/GPL-2.0" }
              { :name "EPLv1", :distribution :repo
                :url "http://www.opensource.org/licenses/EPL-1.0" } ]

  :dependencies [ [org.clojure/clojure  "1.4.0"]
                  [speclj               "2.5.0"] ]

  :plugins      [ [speclj               "2.5.0"]
                  [lein-marginalia      "0.7.1"] ]

  :test-paths   ["spec/"] )
