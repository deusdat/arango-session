(ns arango-session.core-test
  (:require [clojure.test :refer :all]
            [arango-session.core :refer :all]
            [travesedo.database :as tdb]
            [ring.middleware.session.store :refer :all])
            )

(def db-name "arango-session-test")


(def context {:conn {:type :simple
                      :url "http://arangodb:8529"
                      :uname "dev_user"
                      :password "secret"}
               :async :stored
               :db db-name
               :connection-timeout 1000
               :socket-timeout 2000
               :accept-all-ssl? true
               :in-collection "web-sessions"
               :create-collection :yes})
               
(defn server-fixture [f]
	(tdb/create (assoc context :payload {:name db-name}))
	(f)
	(tdb/drop context))
	
(use-fixtures :each server-fixture)

(deftest should-find-no-store
	(let [store (arangodb-store context)]
		(is store)
		(is (read-session store "not-a-key") {})))
		
(deftest should-write-to-store
	(let [store (arangodb-store context)
			payload {:name "virmundi"}
			key (write-session store nil payload)]
		(is (:name (read-session store key)) "virmundi")))
		
(deftest should-update-on-store
	(let [store (arangodb-store context)
			payload {:name "virmundi"}
			dt "2014-12-12"
			key (write-session store nil payload)]
		(is (:name (read-session store key)) "virmundi")
		(write-session store key (assoc payload :last-login dt))
		(is (:last-login (read-session store key)) dt)
		(is (:name (read-session store key)) "virmundi")))	
		
(deftest should-delete-from-store
	(let [store (arangodb-store context)
			payload {:name "virmundi"}
			key (write-session store nil payload)]
		(is (:name (read-session store key)) "virmundi")
		(is (nil? (delete-session store key)))
		(is (read-session store key) {})))		
		