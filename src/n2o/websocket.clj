(ns n2o.websocket
  (:require [ clojure.string                    :as string ]
            [ clojure.core.async                :as async  ]
            [ clojure.core.async.impl.protocols :as proto  ]
            [ clojure.core.cache                :as cache  ]))

; (defonce ws-channel (chan))
(defonce channels (atom #{})) ; TODO : maybe some transients

(defprotocol Sendable 
  (send [this endpoint] 
  "Send method defined properly"))



;; Sendable protocol
;; TODO : implement so it will actually work 

(extend-protocol Sendable 
  java.lang.String
  (send [message endpoint]))



;; Client protocol
;; TODO : implement so it will actually work 

(defprotocol ^:private Client 
  (send-message [this message]
    "Sends a message to the given web socket")
  (close [this]
    "Closes the web socket"))



;; Websocket record 
;; TODO : make it work please 
(defrecord Websocket 
  [ws-url send-channel receive-channel ws-send-channel websocket]
  
  proto/ReadPort 
  (take! [handler]
    (proto/take! (:app-recv-chan channels) handler))
    
  proto/WritePort 
  (put! [val handler]
    (proto/put! (:app-send-chan channels) val handler)))




(defn connect! [channel]
  "Websocket connect handler"
  (log/info "[+] Channel open")
  (swap! channels conj channel))

(defn disconnect! [channel {:keys [code reason]}]
  (log/info "[-] Close code:" code "reason:" reason)
  (swap! channels #(remove #{channel} %)))

(defn notify-channels! [channel message] 
  (doseq [channel @channels]
    (async/put! channel message)))
  
(def websocket-handlers 
  "WS handlers"
  {:on-open connect!
   :on-close disconnect!
   :on-message notify-channels! })

; (defn ws-handler [request]
;   (async/as-channel request websocket-handlers))

; (defn test [url] (js/WebSocket url '[]))