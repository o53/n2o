(ns n2o.core
  {:lang :core.typed}
  (:require [ clojure.string     :as s      ]
            [ clj-time.core      :as t      ]
            [ clj-http.client    :as http   ]
            [ clojure.core.cache :as cache  ]
            [ n2o.server         :as server ]
            ; [ n2o.websocket      :as ws     ]))
            [ n2o.socket         :as socket ]
            [ n2o.config         :as c      ]))
  ; (:gen-class))

(defonce messages (atom []))

(def Req { :cmd "adf"
           :path "qwer"
           :headers '(["qwer" "asdf"])
           :vers "str" })


; (defn ^:private run 
;   "Main server loop"
;   [^Socket socket]
;   (println "[*] Starting n2o server...\n")
;   (server/accept-loop socket))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (let [port 9871]
    (println "[*] Starting n2o server on" port)
    (let [server (-> port server/create-server socket/listen)]
        (server/accept-loop server))))
