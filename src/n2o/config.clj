(ns n2o.config
  (:require [clojure.spec.alpha :as s]
            [clojure.core.typed :as t]))

(t/defalias Input "Input alias" (Sequable Any))

(defonce CHANNEL_BUFFER_SIZE 10)
(defonce MESSAGE-BUFFER-LENGTH 10)

; (defonce DEFAULT-CONFIG { :debug   false 
;                           :verbose false })

(s/def ::opts (s/* (s/cat :opt keyword?
                        ;   :val (s/alt :s string? :b boolean? :i integer?) 
                          :val boolean?)))

; (defn configure [config & {:keys [debug verbose] :or DEFAULT-CONFIG}]
(defn options [config & {:keys [debug verbose] :or {debug false verbose false}}]
  {:pre [(s/valid? ::opts config)] :post [(s/valid? string? %)]}
  (str "val =" config " debug =" debug " verbose =" verbose))





(s/def ::config (s/* (s/cat :property string? 
                            :value (s/alt :s string? 
                                          :b boolean?))))

(defn ^:private set-config [prop val] "Dummy function" (println "set" prop val))

(t/ann configure [Input -> nil])
(defn configure [input]
  ^{:doc "Gives a proper shape for parsed input"}
  {:pre  [(s/valid? (s/coll-of (s/or :s string? :b boolean? :i int?) :kind vector?) input)]
;    :post [(s/valid? nil? %)]}
   :post [true]}
  (let [parsed (s/conform ::config input)]
    (if (= parsed ::s/invalid)
      (throw (ex-info "Invalid flags passed" (s/explain-data ::config input)))
      (for [{prop :prop [_ val] :val} parsed]
        (set-config (subs prop 1) val)))))

(def dummy-config (configure ["-server" "memes" "-verbose" true "-ip" "192.168.0.1"]))