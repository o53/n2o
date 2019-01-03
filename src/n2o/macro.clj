(ns n2o.macro
  (:require [ clojure.core.typed      :as t     ]
            [ clojure.spec.alpha      :as s     ]
            [ clojure.spec.test.alpha :as stest ]))

; (clojure.spec/instrument-all)

(s/fdef uuid :ret string?)
(defmacro uuid "Generates a random uuid" [] (str (java.util.UUID/randomUUID)))
(stest/instrument `uuid)

(t/ann doseq-log [Macro String (Map Any) -> nil])
(defmacro doseq-log 
  [macroname ^String message & args]
  `(println message) ; TODO 
  `(do ~@(map #(list macroname %) args)))

(t/ann ppmap [Integer Function (Map Any) -> nil])
(defn ppmap
 [^Integer grain-size f & colls]
 (apply concat 
   (apply pmap (fn [& pgroups] (doall (apply map f pgroups)))
               (map (partial partition-all grain-size) colls))))

(t/ann dopar [Integer (Map Any) -> nil])
(defmacro dopar 
  [^Integer thread-count [sym coll] & body]
  `(doall (ppmap (fn [values#] (doseq [~sym values#] ~@body))
    (split-eq ~thread-count ~coll))))