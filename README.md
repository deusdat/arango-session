# arango-session
A Ring compatible session manager built on ArangoDB.

## Installing
Add the following dependency to your project.clj file: [arango-session "0.1.0"]

## Usage
First define your ArangoDB context. In this example the collection will be
made if it doesn't already exist.

```clojure
(def context {:conn {:type :simple
                      :url "http://arangodb:8529"
                      :uname "dev_user"
                      :password "secret"}
               :async :stored
               :db "example_db"
               :connection-timeout 1000
               :socket-timeout 2000
               :accept-all-ssl? true
               :in-collection "web-sessions"
               :create-collection :yes})
```

Second, define your web app as per usual using the  session wrapper.
The false parameter indicates that you don't want to create new session
entries with each session save.

```clojure
(def app
  (wrap-session handler {:store (arangodb-store context false)})
```

Third, enjoy your new session store. Keep in mind the rules about ArangoDB.
Don't use :create-collection in a cluster setup; create the session collection
before starting your system.
