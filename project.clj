(defproject runningstats "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"] 
                 [org.clojure/data.csv "1.0.0"] 
                 [clojure.java-time "0.3.2"]]
  :plugins [[lein-kibit "0.1.8"]
            [lein-cljfmt "0.6.7"]]
  :main ^:skip-aot runningstats.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
