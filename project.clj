(defproject grump2 "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/clojurescript "1.10.773"
                  :exclusions [com.google.javascript/closure-compiler-unshaded
                               org.clojure/google-closure-library
                               org.clojure/google-closure-library-third-party]]
                 [thheller/shadow-cljs "2.11.4"]
                 [reagent "0.10.0"]
                 [re-frame "1.1.1"]
                 [ring/ring-core "1.8.1"]
                 [compojure "1.6.2"]
                 [ring/ring-jetty-adapter "1.8.1"]
                 [ring/ring-devel "1.8.1"]
                 [ring "1.8.2"]
                 [ring-cors "0.1.13"]
                 [org.clojure/java.jdbc "0.7.11"]
                 [org.postgresql/postgresql "42.2.16.jre7"]
                 [hiccup "1.0.5"]
                 [cljs-http "0.1.46"]
                 [cljs-ajax "0.8.1"]
      ]
  :plugins [[lein-shadow "0.3.1"]]
  :min-lein-version "2.9.0"
  :source-paths ["src/clj" "src/cljs"]
  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"]

  :shadow-cljs {:nrepl {:port 8777}              
    :builds {:app {:target :browser
      :output-dir "resources/public/js/compiled"
      :asset-path "/js/compiled"
      :modules {:app {:init-fn united.core/init
        :preloads [devtools.preload]}}
      :devtools {:http-root "resources/public"
        :http-port 8280
        }}}}
  
  :shell {:commands {"karma" {:windows         ["cmd" "/c" "karma"]
                              :default-command "karma"}
                     "open"  {:windows         ["cmd" "/c" "start"]
                              :macosx          "open"
                              :linux           "xdg-open"}}}

  :aliases {"dev" 
            ["do" 
              ["shell" "echo" "\"DEPRECATED: Please use lein watch instead.\""]
              ["watch"]]
            "watch" 
              ["with-profile" "dev" "do"
              ["shadow" "watch" "app" "browser-test" "karma-test"]]
            "prod"
            ["do"
             ["shell" "echo" "\"DEPRECATED: Please use lein release instead.\""]
             ["release"]]
            "release"
            ["with-profile" "prod" "do"
              ["shadow" "release" "app"]]
            "build-report" ["with-profile" "prod" "do"
              ["shadow" "run" "shadow.cljs.build-report" "app" "target/build-report.html"]
                ["shell" "open" "target/build-report.html"]]
            "karma"
            ["do"
              ["shell" "echo" "\"DEPRECATED: Please use lein ci instead.\""]
              ["ci"]]
            "ci"
            ["with-profile" "prod" "do"
              ["shadow" "compile" "karma-test"]
              ["shell" "karma" "start" "--single-run" "--reporters" "junit,dots"]]}

  :profiles
  {:dev
   {:dependencies [[binaryage/devtools "1.0.2"]]
    :source-paths ["dev"]}

   :prod {}
   
}

  :prep-tasks []
  :main ^:skip-aot united.web
  )
