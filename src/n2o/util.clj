(ns n2o.util
  (:require [clojure.core.typed      :as t    ]
            [clojure.spec.alpha      :as s    ]
            [clojure.spec.gen.alpha  :as gen  ]
            [clojure.spec.test.alpha :as stest]))

(defn ->sym [x] (@#'s/->sym x))

(def ansi-codes ; TODO : static 
  "Ansi color codes used for prettifying output"
  #{:black   "\u001b[30m" :gray           "\u001b[1m\u001b[30m"
    :red     "\u001b[31m" :bright-red     "\u001b[1m\u001b[31m"
    :green   "\u001b[32m" :bright-green   "\u001b[1m\u001b[32m"
    :yellow  "\u001b[33m" :bright-yellow  "\u001b[1m\u001b[33m"
    :blue    "\u001b[34m" :bright-blue    "\u001b[1m\u001b[34m"
    :magenta "\u001b[35m" :bright-magenta "\u001b[1m\u001b[35m"
    :cyan    "\u001b[36m" :bright-cyan    "\u001b[1m\u001b[36m"
    :white   "\u001b[37m" :bright-white   "\u001b[1m\u001b[37m"
    :default "\u001b[39m" :reset          "\u001b[0m" })

(t/ann insert-at [(Vec T) Integer T -> Vec T]) ; TODO : dependent types probably 
(defn insert-at [vector index value]
  "Inserts value at a specific index inside vector"
  (-> (subvec vector 0 index) 
      (conj value)
      (into (subvec vector index))))

(def to-bytes (comp bytes byte-array (map byte)))

; TODO : unused
(defn in? [coll element] (some #(= element %) coll))


(s/fdef random 
  :args (s/and (s/cat :start int? :end int?) #(< (:start %) (:end %)))
  :ret int? 
  :fn (s/and #(>= (:ret %) (-> % :args :start))
             #(<  (:ret %) (-> % :args :end))))

(t/ann random [Integer Integer -> Integer])
(defn random 
  "Random number in [start;end)"
  [^Integer start ^Integer end]
  {:pre  [(s/and (s/valid? int? start) (s/valid? int? end))]
   :post [(s/valid? int? %)]}
  (+ start (long (rand (- end start)))))

(stest/instrument `random)













; Service invokation request 

(s/def ::query string?)
(s/def ::request (s/keys :req [::query]))
(s/def ::result (s/coll-of string? :gen-max 3))
(s/def ::error int?)
(s/def ::response (s/or :ok (s/keys :req [::result]) :err (s/keys :req [::error])))

(s/fdef invoke-service
  :args (s/cat :service any? :request ::request)
  :ret ::response)

(s/fdef run-query
  :args (s/cat :service any? :query string?)
  :ret (s/or :ok ::result :err ::error))

(defn invoke-service [service request]
  ;; invokes remote service
  )

(defn run-query [service query]
  (let [{::keys [result error]} (invoke-service service {::query query})]
    (or result error)))