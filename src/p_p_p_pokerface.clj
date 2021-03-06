(ns p-p-p-pokerface)

(def replacements {\T 10 \J 11 \Q 12 \K 13 \A 14})


(defn rank [card]
  (let [[rank-char _] card]
    (if (Character/isDigit rank-char)
      (Integer/valueOf (str rank-char))
      (replacements rank-char))))

(defn suit [card]
  (let [[_ second-char] card]
    (str second-char)))

(defn hand->rank-frequencies [hand]
  (frequencies (map rank hand)))

(defn hand->suit-frequencies [hand]
  (frequencies (map suit hand)))

(defn pair? [hand]
  (< 1 (apply max (vals (hand->rank-frequencies hand)))))

(defn three-of-a-kind? [hand]
  (< 2 (apply max (vals (hand->rank-frequencies hand)))))

(defn four-of-a-kind? [hand]
  (< 3 (apply max (vals (hand->rank-frequencies hand)))))

(defn flush? [hand]
  (= 5 (apply max (vals (hand->suit-frequencies hand)))))

(defn full-house? [hand]
  (let [rank-freqs (vals (hand->rank-frequencies hand))]
    (or (= [2 3] rank-freqs) (= [3 2] rank-freqs))))

(defn two-pairs? [hand]
  (let [descending-rank-freqs (sort > (vals (hand->rank-frequencies hand)))]
      (or (= [2 2] (take 2 descending-rank-freqs)) (= [4] (take 1 descending-rank-freqs)))))

(defn straight-impl? [ranks]
  (let [sorted-ranks (sort ranks)]
    (let [lowest_rank (nth sorted-ranks 0)]
      (= sorted-ranks (range lowest_rank (+ lowest_rank 5))))))

(defn straight? [hand]
  (let [ranks-high-ace (map rank hand)]
    (let [ranks-low-ace (replace {14 1} ranks-high-ace)]
      (or (straight-impl? ranks-high-ace) (straight-impl? ranks-low-ace)))))



(defn straight-flush? [hand]
  (and (flush? hand) (straight? hand)))


(defn high-card? [hand]
  true) ; All hands have a high card.


(defn value [hand]
  (let [checkers #{[high-card? 0]  [pair? 1]
                 [two-pairs? 2]  [three-of-a-kind? 3]
                 [straight? 4]   [flush? 5]
                 [full-house? 6] [four-of-a-kind? 7]
                 [straight-flush? 8]}]
    (let [evaluate-checkers (map (fn [x] x) checkers)] ;;run anonym fn for each checker, anon fn is formed in the next row
      (let [matching-checkers (filter (fn [x] ((first x) hand)) evaluate-checkers)] ;;filters matching checkers, (first x) points to the first part of the checker macro match pair i.e. 'high-card'
        (let [values-from-matching-checkers (map second matching-checkers)]
          (apply max values-from-matching-checkers))))))

