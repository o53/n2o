(ns n2o.socket 
  {:lang :core.typed}
  (:require [clojure.core.typed :as t]
            [clojure.java.io :refer [writer reader]])
  ; (:refer-clojure :exclude [read-line])
  (:import (java.net Socket ServerSocket)
           (java.io BufferedWriter BufferedReader)
           (clojure.lang Seqable)))

; (t/defalias Socket "java.net/Socket type alias" java.net/Socket)
; (t/defalias ServerSocket "java.net/ServerSocket type alias" java.net/ServerSocket)

(t/ann ^:no-check clojure.java.io/writer [Socket -> BufferedWriter])
(t/ann ^:no-check clojure.java.io/reader [Socket -> BufferedReader])
(t/ann ^:no-check clojure.core/line-seq  [BufferedReader -> (Seqable String)])

(t/ann create-socket [String Integer -> Socket])
(defn create-socket 
  ^{:doc "Connect a socket to a remote host"} 
  [^String host ^Integer port] 
  (Socket. host port))

(t/ann close! [Socket -> nil])
(defn close! 
  ^{:doc "Closes the socket"} 
  [^Socket socket] 
  (.close socket))

(t/ann write-to-buffer [BufferedWriter String -> nil])
(defn ^:private write-to-buffer
  ^{:doc "Write a string to a BufferedWriter"}
  [^BufferedWriter stream ^String string]
  (.write stream string)
  (.flush stream))

(t/ann write-to [Socket String -> nil])
(defn write-to
  ^{:doc "Send a string over the socket"}
  [^Socket socket ^String message]
  (write-to-buffer (writer socket) message))

(t/ann write-line [Socket String -> nil])
(defn write-line
  ^{:doc "Send a line over the socket"}
  [^Socket socket ^String message]
  (write-to socket (str message "\r\n")))

; TODO : understand 
(t/ann get-reader [Socket -> BufferedReader])
(def ^:private get-reader ^{:doc "Get the BufferedReader for a socket"}
  (memoize (t/ann-form
             (fn [^Socket socket] (reader socket))
             (t/IFn [Socket -> BufferedReader]))))

(t/ann read-char [Socket -> Character])
(defn read-char ^{:doc "Read a single character from a socket"} []
  (let [read-buffer (t/ann-form 
                      (fn [^BufferedReader stream] (.read stream)) 
                      (t/IFn [BufferedReader -> Integer]))]
    (comp char read-buffer get-reader)))
    ; (-> socket get-reader read-buffer char)))

(t/ann read-lines [Socket -> (Seqable String)])
(defn read-lines ^{:doc "Read all the lines from input steram"} []
  (comp line-seq get-reader))
;   (line-seq (get-reader socket)))

;; core.typed is paranoid about Java methods returning nil, but lets you
;; override that if you're fairly sure that it's not going to.
(t/non-nil-return java.io.BufferedReader/readLine :all)

(t/ann read-line [Socket -> String])
(defn read-line ^{:doc "Read a line from the given socket"} []
  (let [read-line-from-reader (t/ann-form
                                (fn [^BufferedReader reader] (.readLine reader))
                                (t/IFn [BufferedReader -> String]))]
    (comp read-line-from-reader get-reader)))

(t/non-nil-return java.net.ServerSocket/accept :all) ; TODO : wtf 
(t/ann listen [ServerSocket -> Socket])
(defn listen 
  ^{:doc "Blocks thread and waits for a connection from another socket"}
  [^ServerSocket socket]
  (.accept socket))
