(ns arango-session.core
	(:require [travesedo.document :as tdoc]
				[ring.middleware.session.store :as ringstore]))

(deftype ArangodbStore [ctx auto-key-change?]
	ringstore/SessionStore
	
	(read-session [_ key]
		(if-let [entity (and key
									(tdoc/find-by-id (assoc ctx :_id key)))]
			entity {}))
		
	(write-session [_ key data]
		(let [full-ctx (assoc ctx :_id key :payload data)
				action (cond (and key auto-key-change?) #((tdoc/create %) 
																		(tdoc/delete %))
								 (and key (false? auto-key-change?)) tdoc/patch-doc
								 :else tdoc/create)]
				(:_id (action full-ctx))))
	
		
	(delete-session [_ key]
		(tdoc/delete (assoc ctx :_id key))
		nil)
)

(defn arangodb-store
	"Creates a session store using the passed in context as defined in the 
	travesedo driver. auto-key-change causes the session to get a new id one
	every change. Specify the collection you want to store the sessions in
	via the :in-collection slot in the ctx."
	([ctx] (arangodb-store ctx false))
	([ctx auto-key-change?]
	(ArangodbStore. ctx auto-key-change?) ))