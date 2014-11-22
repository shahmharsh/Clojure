(defproject video_store "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [seesaw "1.4.4"]
                 [me.raynes/fs "1.4.4"]
                 [clj-time "0.8.0"]]
  :main ^:skip-aot video-store.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}}
  :plugins [[com.jakemccrary/lein-test-refresh "0.5.1"]])
