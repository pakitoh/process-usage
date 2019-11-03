(ns metered.usage
  (:require [metered.usage :refer :all]))

;; TESTS #1
(def events
  [{:usage/event     :usage.event/create
    :usage/resource  :usage.resource/object
    :usage/uuid      #uuid "d8377d93-db71-488a-b894-54a962760bea"
    :usage/account   #uuid "ee12577c-983f-4729-a0e9-c5789a906c04"
    :usage/timestamp #inst "2017-03-10T00:00:00.000-00:00"}
   {:usage/event     :usage.event/destroy
    :usage/resource  :usage.resource/object
    :usage/uuid      #uuid "d8377d93-db71-488a-b894-54a962760bea"
    :usage/account   #uuid "ee12577c-983f-4729-a0e9-c5789a906c04"
    :usage/timestamp #inst "2017-03-10T01:00:00.000-00:00"}])

(assert (= (process-usage events)
           [{:usage/resource  :usage.resource/object
             :usage/uuid      #uuid "d8377d93-db71-488a-b894-54a962760bea"
             :usage/account   #uuid "ee12577c-983f-4729-a0e9-c5789a906c04"
             :usage/duration  60}]))

;; TEST #2
(def more-events
  [{:usage/event     :usage.event/destroy
    :usage/resource  :usage.resource/object
    :usage/uuid      #uuid "aaaaaaaa-db71-488a-b894-54a962760bea"
    :usage/account   #uuid "11111111-983f-4729-a0e9-c5789a906c04"
    :usage/timestamp #inst "2016-01-01T01:00:00.000-00:00"}
   {:usage/event     :usage.event/create
    :usage/resource  :usage.resource/object
    :usage/uuid      #uuid "aaaaaaaa-db71-488a-b894-54a962760bea"
    :usage/account   #uuid "11111111-983f-4729-a0e9-c5789a906c04"
    :usage/timestamp #inst "2017-01-01T00:00:00.000-00:00"}
   {:usage/event     :usage.event/create
    :usage/resource  :usage.resource/object
    :usage/uuid      #uuid "aaaaaaaa-db71-488a-b894-54a962760bea"
    :usage/account   #uuid "11111111-983f-4729-a0e9-c5789a906c04"
    :usage/timestamp #inst "2017-01-01T00:00:01.000-00:00"}
   {:usage/event     :usage.event/destroy
    :usage/resource  :usage.resource/object
    :usage/uuid      #uuid "aaaaaaaa-db71-488a-b894-54a962760bea"
    :usage/account   #uuid "11111111-983f-4729-a0e9-c5789a906c04"
    :usage/timestamp #inst "2017-01-01T01:00:00.000-00:00"}
   {:usage/event     :usage.event/create
    :usage/resource  :usage.resource/vm
    :usage/uuid      #uuid "cccccccc-db71-488a-b894-54a962760bea"
    :usage/account   #uuid "22222222-983f-4729-a0e9-c5789a906c04"
    :usage/timestamp #inst "2017-01-01T20:00:00.000-00:00"}
   {:usage/event     :usage.event/create
    :usage/resource  :usage.resource/vm
    :usage/uuid      #uuid "bbbbbbbb-db71-488a-b894-54a962760bea"
    :usage/account   #uuid "11111111-983f-4729-a0e9-c5789a906c04"
    :usage/timestamp #inst "2017-01-01T10:00:00.000-00:00"}
   {:usage/event     :usage.event/destroy
    :usage/resource  :usage.resource/vm
    :usage/uuid      #uuid "bbbbbbbb-db71-488a-b894-54a962760bea"
    :usage/account   #uuid "11111111-983f-4729-a0e9-c5789a906c04"
    :usage/timestamp #inst "2017-01-01T12:00:00.000-00:00"}
   {:usage/event     :usage.event/destroy
    :usage/resource  :usage.resource/object
    :usage/uuid      #uuid "aaaaaaaa-db71-488a-b894-54a962760bea"
    :usage/account   #uuid "11111111-983f-4729-a0e9-c5789a906c04"
    :usage/timestamp #inst "2017-01-01T01:00:00.000-00:00"}
   {:usage/event     :usage.event/create
    :usage/resource  :usage.resource/object
    :usage/uuid      #uuid "aaaaaaaa-db71-488a-b894-54a962760bea"
    :usage/account   #uuid "11111111-983f-4729-a0e9-c5789a906c04"
    :usage/timestamp #inst "2017-01-01T02:00:00.000-00:00"}
   {:usage/event     :usage.event/destroy
    :usage/resource  :usage.resource/object
    :usage/uuid      #uuid "aaaaaaaa-db71-488a-b894-54a962760bea"
    :usage/account   #uuid "11111111-983f-4729-a0e9-c5789a906c04"
    :usage/timestamp #inst "2017-01-01T01:00:00.000-00:00"}
   {:usage/event     :usage.event/destroy
    :usage/resource  :usage.resource/vm
    :usage/uuid      #uuid "cccccccc-db71-488a-b894-54a962760bea"
    :usage/account   #uuid "22222222-983f-4729-a0e9-c5789a906c04"
    :usage/timestamp #inst "2017-01-01T23:00:00.000-00:00"}])

(assert (= (process-usage more-events)
           [#:usage{:account #uuid "11111111-983f-4729-a0e9-c5789a906c04",
                    :uuid #uuid "aaaaaaaa-db71-488a-b894-54a962760bea",
                    :resource :usage.resource/object,
                    :duration 60}
            #:usage{:account #uuid "11111111-983f-4729-a0e9-c5789a906c04",
                    :uuid #uuid "bbbbbbbb-db71-488a-b894-54a962760bea",
                    :resource :usage.resource/vm,
                    :duration 120}
            #:usage{:account #uuid "22222222-983f-4729-a0e9-c5789a906c04",
                    :uuid #uuid "cccccccc-db71-488a-b894-54a962760bea",
                    :resource :usage.resource/vm,
                    :duration 180}]))
