(ns textadventure.core)

(def locations (atom {:home {:description "You're at home."
                             :exits {:south :garden}
                             :items [{:key {:description "Shiny key"}}
                                     {:book {:description "Very heavy book"}}]}
                      :garden {:description "You're in the garden."
                               :exits {:north :home
                                       :east :shed}}
                      :shed {:description "You're in the shed"}
                      :exits {:west :garden}}))

(defn pick
  [state item]
  (let [currently-at (@locations (:current-location @game-state))
        local-items (:items currently-at)]
    (when-let [the-item (some item local-items)]
      (swap! state (fn [current-game-state]
                     (merge current-game-state
                            {:inventory (conj (:inventory current-game-state) the-item)} )
                     ))
      (swap! locations (fn [current-location]
                         (update-in current-location [(:current-location @game-state) :items]
                                    (fn [coll] (remove the-item coll))
                                    ))))))

(defn move
  [state direction]
  (swap! state (fn [current-game-state]
                 (let [currently-at (@locations (:current-location current-game-state))]
                   (if-let [new-place (get-in currently-at [:exits direction])]
                     (merge current-game-state {:current-location new-place})
                     current-game-state)))))

(defn look
  [state]
  (let [currently-at (@locations (:current-location @game-state))]
    ( println "You see:")
    (doall (map clojure.pprint/pprint (get-in currently-at [:items] ))))
  )

(def game-state (atom {:current-location :home
                       :inventory {}}))
(move game-state :south)
(move game-state :north)
(look game-state)
(pick game-state :key)
(clojure.pprint/pprint game-state)
