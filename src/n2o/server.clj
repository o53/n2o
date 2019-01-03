(ns n2o.server
  {:lang :core.typed}
  (:import (clojure.lang Keyword)
           (java.net Socket ServerSocket))
  (:require ; [ clojure.string        :as s      ]
            [ clojure.string :as string ]
            [ clojure.spec.alpha    :as s      ]
            [ clojure.core.typed    :as t      ]
            [ clojure.spec.test.alpha :as stest ]
            [ clojure.tools.logging :as log    ]
            [ clojure.core.cache    :as cache  ]
            [ n2o.socket            :as socket ]
            [ n2o.http              :as http   ]
            [ n2o.config            :as c      ]
            [ n2o.util              :as util   ]
            [ n2o.macro             :as macro  ]))

; (clojure.spec/instrument-all)

(t/defalias Request  "Request type alias"  String)
(t/defalias Response "Response type alias" Any) ; TODO
(t/defalias Message  "Message type alias"  Any)
(t/defalias Headers  "Header type alias"   (Map String String))

(def ^:private response-table {:switching-protocols    "101"
                               :ok                     "200"
                               :bad-request            "400"
                               :not-found              "404"})

(def ^:private routes {"/" "/index"
                       })



(s/fdef route 
  :args (s/cat :path string?)
  :ret string?)

; TODO : a proper combinator
(t/ann route [String -> String])
(defn route 
  "Get route from the given path" 
  [^String path]
  {:pre (s/valid? string? path)
   :post (s/valid? string? %)}
  (if-not (contains? routes path)
    (if (.startsWith path "/ws") 
      (subs path 3) 
      path)
    (get routes path)))

; (defn error-code [code]
;   (capitalize (str/split code #"-")))

(t/ann response-code [Keyword String -> String])
(defn response-code [^Keyword code ^String comment]
  (some-> (get response-table code) 
          (str "[ " (->> (string/split (name code) #"-") 
                         (map string/capitalize)
                         (string/join " "))
               "]: " comment)))

; (defn parse-request [request]
;   (let [parsed (tokens request "\r\n")
;         error  (error-code :bad-request "Malformed HTTP request")]
;     (unwrap-or parsed error)))

(t/ann send [Socket Message -> nil])
(defprotocol Sendable 
  (send [^Socket socket ^String message]))

(extend-protocol Sendable 
  String 
  (send [^Socket socket ^String message] (socket/write-line socket message)))

; (defn send [^Socket socket ^String message] (socket/write-line socket message))

(t/ann write-headers [Map String String -> String]) ; TODO 
; (defn write-headers [headers] (doseq [[k v] headers] (println (str k ": " v "\r\n"))))
(defn write-headers [headers] (map (fn [[k v]] (str k ": " v "\r\n")) headers))

(t/ann send-response [Socket Response -> nil])
(defn ^:private send-response 
    [^Socket socket ^Response {:keys [status headers body]} response]
    (send socket 
      (str "HTTP/1.1" status " " 
           (get response-table status) "\r\n" 
           (write-headers headers) "\r\n")))

(t/ann send-error [Socket Keyword Message -> nil])
(defn ^:private send-error [^Socket socket ^Keyword code ^String body]
  (println body)
  (send-response socket {:status code :body (util/to-bytes body)})
  (socket/close! socket))

(t/ann file-response [String -> Any])
(defn file-response [^String path] 
  ; { :status 200 :body (slurp path)}
  (let [contents (slurp path)
        code (if (nil? contents) 404 200)]
    { :status code :body contents }))

(t/ann header [String Request -> Maybe String])
(defn header [^String request-name] (comp :headers ::request-name))

(t/ann need-upgade [Request -> Bool])
(defn need-upgrade [] (comp (= "websocket") string/lower-case (header "Upgrade")))

; (defn serve [^Socket socket]
;   (let [request (-> parse-request (recv 2048))
;         path (:path request)
;         routed (route path)]
;     (if (upgrade-needed request)
;       {request (upgrade socket request)}
;       (try ))))

; (defn switch 
;   "Get an appropriate socket"
;   [^Socket socket]
;   (if (serve socket)
;     ()))

(defn accept-loop [^Socket socket] "Ok.")


(s/fdef create-server 
  :args (s/cat :port int?)
;   :ret server-socket?
;   :fn )
)

(t/ann create-server [Integer -> ServerSocket])
(defn create-server
  "Initialise a ServerSocket on localhost using a port.
  Passing in 0 for the port will automatically assign a port based on what's
  available."
  [^Integer port]
  (println "waiting for the connection to be made...")
  (ServerSocket. port))

(stest/instrument `create-server)