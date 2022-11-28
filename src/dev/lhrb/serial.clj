(ns dev.lhrb.serial
  (:import (com.fazecast.jSerialComm SerialPort))
  (:require [clojure.string :as str]
            [clojure.java.io :as io]))

(defn list-serial-ports []
  (map #(.getSystemPortName %) (SerialPort/getCommPorts)))

(defn describe-port [serial-port]
  #:port{:boudrate (.getBaudRate serial-port)
       :cts (.getCTS serial-port)
       :dcd (.getDCD serial-port)
       :dsr (.getDSR serial-port)
       :dtr (.getDTR serial-port)
       :name (.getSystemPortName serial-port)
       :path (.getSystemPortName serial-port)
       :read-timeout  (.getReadTimeout serial-port)
       :write-timeout (.getWriteTimeout serial-port)})

(defn slurp-file
  [file]
  (-> file
      (io/file)
      (.toPath)
      (java.nio.file.Files/readAllBytes)))

(defn open-port
  [port-name]
  (doto (SerialPort/getCommPort port-name)
  (.openPort)))

(defn listen-blocking
  [port]
  (loop [n 10]
    (println "try to read from serial port n =" n)
    (if (or (= 0 n) ; timeout
            (< 0 (.bytesAvailable port)))
      (let [len (.bytesAvailable port)
            arr (byte-array len)]
        (.readBytes port arr len)
        (String. arr))
      (do
        (Thread/sleep 100)
        (recur (dec n))))))

(defn send-cmd
  [port ^String cmd]
  (let [arr (.getBytes cmd)
        len (count arr)]
    (.writeBytes port arr len)
    (listen-blocking port)))



(comment


  ;; socat -d -d pty,raw,echo=0 pty,raw,echo=0

  (jdoc/javadoc SerialPort)

  (require '[clojure.repl :refer :all])
  (require '[clojure.java.javadoc :as jdoc])
  (require '[clojure.reflect :as reflect])

  (def p (SerialPort/getCommPort "/dev/pts/3"))

  (->> (:members (reflect/reflect p))
       (map :name)
       (map str)
       (filter #(str/starts-with? % "get" ))
       (distinct)
       (sort))

  (count (.getBytes "hallo"))

  (let [arr (.getBytes "hallo\n")]
    (.writeBytes p arr (count arr)))

  (.bytesAvailable p)


  (def arr (byte-array (.bytesAvailable p)))
  (.readBytes p arr 8)

  (String. arr)

 ,)
