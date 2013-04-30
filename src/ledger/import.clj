(ns ledger.import
  (:require [clojure.java.io :as io]
            [clojure.string :as str])
  (:import (java.text SimpleDateFormat)))

(defn parse-csv [f]
  (let [parser (SimpleDateFormat. "MM/dd/yyyy")]
    (for [line (str/split-lines (slurp f))
          :let [[_ _ date _ description category amount] (str/split line #",")]]
      {:date (.parse parser date)
       :description description
       :category category
       :amount (bigdec (if (= \" (first amount))
                         (.substring amount 1 (dec (count amount)))
                         amount))})))

(defn write-ledger [out account entries]
  (let [formatter (SimpleDateFormat. "yyyy-MM-dd")]
   (with-open [out (io/writer out)]
     (doseq [{:keys [date description category amount]} (sort-by :date entries)]
       (.write out (str (.format formatter date) " " description "\n"))
       (.write out (str "\t" category "\t" (* -1 amount) "\n"))
       (.write out (str "\t" account "\n"))))))
