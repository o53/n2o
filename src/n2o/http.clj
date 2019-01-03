(ns n2o.http
  {:lang :core.typed}
  (:require [ clojure.spec.alpha      :as s     ]
            [ clojure.core.typed      :as t     ]
            [ clojure.spec.test.alpha :as stest ]))

(defonce options {:timeout 200 
                  :auth ["user" "pass"]
                  :query {:first-param "value" 
                          :second-param ["v1" "v2"]}
                  :user-agent "Mozilla/5.0 (X11; Fedora; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko)"
                  :headers {"X-Header" "Value"}})

(s/def :event/type      keyword?)
(s/def :event/timestamp int?)
(s/def :search/url      string?)
(s/def :error/message   string?)
(s/def :error/code      int?)

(defmulti event-type :event/type)
(defmethod event-type :event/search [_] 
  (s/keys :req [:event/type :event/timestamp :search/url]))
(defmethod event-type :event/error [_]
  (s/keys :req [:event/type :event/timestamp :error/message :error/code]))

(s/def :event/event (s/multi-spec event-type :event/type))




;; Query 

(s/def ::query string?)
(s/def ::request (s/keys :req [::query]))
(s/def ::result (s/coll-of string? :gen-max 3))
(s/def ::error int?)
(s/def ::response (s/or :ok (s/keys :req [::result]) :err (s/keys :req [::error])))



(s/fdef invoke 
  :args (s/cat :service any? :request ::request
  :ret ::response))

(t/ann invoke [Any ::request -> ::response])
(defn invoke [service request]
  {:pre  [s/valid? ::request request]
   :post [s/valid? ::response %]}
   {:result ["qwer" "asdf"]})

(stest/instrument `invoke)



(s/fdef run-query 
  :args (s/cat :service any? :query string?)
  :ret (s/or :ok ::result :err ::error))

(t/ann run-query [Any String -> Any]) ; TODO
(defn run-query [service ^String query]
  {:pre  [s/valid? string? query]
   :post [s/valid? (s/or :ok ::result :err ::error) %]}
  (let [{::keys [result error]} (invoke service {::query query})]
    (or result error)))

(stest/instrument `run-query)

; (defn meme [msg :- String] (println (str "meme: " msg)))