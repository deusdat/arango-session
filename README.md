# arango-session
A Ring compatible session manager built on ArangoDB.

## Usage
First define your ArangoDB context. In this example the collection will be
made if it doesn't already exist.

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