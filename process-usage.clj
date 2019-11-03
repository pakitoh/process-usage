(ns metered.usage)

;; EVENT GROUPING

(defn group-by-account [events]
  (group-by :usage/account events))

(defn map-kv [f coll]
  (reduce-kv
   (fn [acc k v] (assoc acc k (f v)))
   (empty coll)
   coll))

(defn group-by-resource [events]
  (map-kv #(group-by :usage/uuid %) events))

(defn group-events [events]
  (->> events
       group-by-account
       group-by-resource))


;; EVENT PROCESSING

(defn sort-by-timestamp [events]
  (sort-by :usage/timestamp events))

(defn remove-duplicates [events]
  (map first (partition-by :usage/event events)))

(defn to-mins [millis]
  (/ millis 60000))

(defn compute-duration [acc x]
  (to-mins
   (+ (:usage/duration acc)
      (- (.getTime (:usage/timestamp x))
         (.getTime (:usage/start-time acc))))))

(defn create-event? [e]
  (= (:usage/event e) :usage.event/create))

(defn process-create [partial-resul e]
  (assoc partial-resul
         :usage/start-time
         (:usage/timestamp e)))

(defn destroy-event? [partial-resul e]
  (and
   (= (:usage/event e) :usage.event/destroy)
   (:usage/start-time partial-resul)))

(defn process-destroy [partial-resul e]
  (assoc partial-resul
         :usage/duration
         (compute-duration partial-resul e)))

(defn calculate-usage [events]
  (let [event (first events)]
    (reduce (fn [partial-resul e]
              (cond
                (create-event? e) (process-create partial-resul e)
                (destroy-event? partial-resul e) (process-destroy partial-resul e)
                :else partial-resul))
            {:usage/account (:usage/account event)
             :usage/uuid (:usage/uuid event)
             :usage/resource (:usage/resource event)
             :usage/duration 0}
            events)))

(defn clean-up [resul]
  (dissoc resul :usage/start-time))

(defn process-events [events]
  (->> events
       sort-by-timestamp
       remove-duplicates
       calculate-usage
       clean-up))

(defn process-events-per-resource [events]
  (vals (map-kv process-events events)))

(defn process-events-per-account [events]
  (vals (map-kv process-events-per-resource events)))


;; MAIN

(defn process-usage [events]
  (->> events
       group-events
       process-events-per-account
       flatten
       vec))

