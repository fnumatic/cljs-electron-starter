(ns elctr-starter.views.utils
  (:require 
            [reagent.core :as r]
            [clojure.string :as str]
            ))


(defn merge-attr [base-class class style attr]
  (merge
   {:class (conj base-class class)
    :style style}
   attr))

(defn v-box
  [& {:keys [  children class style attr]}]
  (into 
   [:div
    (merge-attr [:v-box :flex :flex-col :w-full] class style attr)]
   children))

(defn h-box
  [& {:keys [  children class style attr]}]
  (into 
   [:div
    (merge-attr [:h-box :flex :flex-row  :w-full] class style attr)]
   children))


(defn- flex-child-style
    [size]
  ;; TODO: Could make initial/auto/none into keywords???
  (let [split-size      (str/split (str/trim size) #"\s+")                  ;; Split into words separated by whitespace
        split-count     (count split-size)
        _               (assert (contains? #{1 3} split-count) "Must pass either 1 or 3 words to flex-child-style")
        size-only       (when (= split-count 1) (first split-size))         ;; Contains value when only one word passed (e.g. auto, 60px)
        split-size-only (when size-only (str/split size-only #"(\d+)(.*)")) ;; Split into number + string
        [_ num units]   (when size-only split-size-only)                    ;; grab number and units
        pass-through?   (nil? num)                                          ;; If we can't split, then we'll pass this straign through
        grow-ratio?     (or (= units "%") (= units "") (nil? units))        ;; Determine case for using grow ratio
        grow            (if grow-ratio? num "0")                            ;; Set grow based on percent or integer, otherwise no grow
        shrink          (if grow-ratio? "1" "0")                            ;; If grow set, then set shrink to even shrinkage as well
        basis           (if grow-ratio? "0px" size)                         ;; If grow set, then even growing, otherwise set basis size to the passed in size (e.g. 100px, 5em)
        flex            (if (and size-only (not pass-through?))
                          (str grow " " shrink " " basis)
                          size)]
    {:flex flex}))

(defn- icon-span2 [icon]
  [:span.icon
   icon])

(defn icon-btn3 [icon onClick opts]
  [:a.button
   (assoc opts :on-click onClick)
   (when icon
     [icon-span2 icon])])

(defn drag-handle
  "Return a drag handle to go into a vertical or horizontal splitter bar:
    orientation: Can be :horizonal or :vertical
    over?:       When true, the mouse is assumed to be over the splitter so show a bolder color"
  [orientation over?]
  (let [vertical? (= orientation :vertical)
        length    "20px"
        width     "8px"
        pos1      "3px"
        pos2      "3px"
        color     (if over? "#999" "#ccc")
        border    (str "solid 1px " color)
        flex-flow (str (if vertical? "row" "column") " nowrap")]
    [:div {:class [:flex]
           :style (merge  {:flex-flow flex-flow}
                          {:width  (if vertical? width length)
                           :height (if vertical? length width)
                           :margin "auto"})}
     [:div {:style (if vertical?
                     {:width pos1   :height length :border-right  border}
                     {:width length :height pos1   :border-bottom border})}]
     [:div {:style (if vertical?
                     {:width pos2   :height length :border-right  border}
                     {:width length :height pos2   :border-bottom border})}]]))

(defn sum-scroll-offsets
  "Given a DOM node, I traverse through all ascendant nodes (until I reach body), summing any scrollLeft and scrollTop values
   and return these sums in a map"
  [node]
  (loop [current-node    (.-parentNode node) ;; Begin at parent
         sum-scroll-left 0
         sum-scroll-top  0]
    (if (not= (.-tagName current-node) "BODY")
      (recur (.-parentNode current-node)
             (+ sum-scroll-left (.-scrollLeft current-node))
             (+ sum-scroll-top  (.-scrollTop  current-node)))
      {:left sum-scroll-left
       :top  sum-scroll-top})))

(defn get-element-by-id
  [id]
  (.getElementById js/document id))

(defn h-split
  "Returns markup for a horizontal layout component"
  [& {:keys [size width height on-split-change initial-split splitter-size margin]
      :or   {size "auto" initial-split 50 splitter-size "8px"}
      :as   args}]
  (let [container-id         (gensym "h-split-")
        split-perc           (r/atom (js/parseInt initial-split)) ;; splitter position as a percentage of width
        dragging?            (r/atom false)                       ;; is the user dragging the splitter (mouse is down)?
        over?                (r/atom false)                       ;; is the mouse over the splitter, if so, highlight it

        stop-drag            (fn []
                               (when on-split-change (on-split-change @split-perc))
                               (reset! dragging? false))

        calc-perc            (fn [mouse-x]                                                 ;; turn a mouse y coordinate into a percentage position
                               (let [container  (get-element-by-id container-id)           ;; the outside container
                                     offsets    (sum-scroll-offsets container)             ;; take any scrolling into account
                                     c-width    (.-clientWidth container)                  ;; the container's width
                                     c-left-x   (.-offsetLeft container)                   ;; the container's left X
                                     relative-x (+ (- mouse-x c-left-x) (:left offsets))]  ;; the X of the mouse, relative to container
                                 (* 100.0 (/ relative-x c-width))))                        ;; do the percentage calculation

        <html>?              #(= % (.-documentElement js/document))                        ;; test for the <html> element

        mouseout             (fn [event]
                               (if (<html>? (.-relatedTarget event))                       ;; stop drag if we leave the <html> element
                                 (stop-drag)))

        mousemove            (fn [event]
                               (reset! split-perc (calc-perc (.-clientX event))))

        mousedown            (fn [event]
                               (.preventDefault event)                                    ;; stop selection of text during drag
                               (reset! dragging? true))

        mouseover-split      #(reset! over? true) ;; true CANCELs mouse-over (false cancels all others)
        mouseout-split       #(reset! over? false)

        make-container-attrs (fn [class style attr in-drag?]
                               (merge {:class (conj [:rc-h-split :flex] class)
                                       :id    container-id
                                       :style (merge (flex-child-style size)
                                                     {:flex-flow "row nowrap"}
                                                     {:margin margin
                                                      :width  width
                                                      :height height}
                                                     style)}
                                      (when in-drag?                             ;; only listen when we are dragging
                                        {:on-mouse-up     stop-drag
                                         :on-mouse-move   mousemove 
                                         :on-mouse-out    mouseout})
                                      attr))

        make-panel-attrs     (fn [class in-drag? percentage]
                               {:class (conj [:flex :overflow-hidden]  class)
                                :style (merge (flex-child-style (str percentage " 1 0px"))
                                              (when in-drag? {:pointer-events "none"}))})

        make-splitter-attrs  (fn [class]
                               {:class         (conj [:flex :cursor-colresize] class)
                                :on-mouse-down   mousedown 
                                :on-mouse-over   mouseover-split
                                :on-mouse-out    mouseout-split
                                :style         (merge (flex-child-style (str "0 0 " splitter-size))
                                                      (when @over? {:background-color "#f8f8f8"}))})]

    (fn
      [& {:keys [panel-1 panel-2 _size _width _height _on-split-change _initial-split _splitter-size _margin class style attr]}]
      [:div (make-container-attrs class style attr @dragging?)
       [:div (make-panel-attrs "rc-h-split-top" @dragging? @split-perc)
        panel-1]
       [:div (make-splitter-attrs "rc-h-split-splitter")
        [drag-handle :vertical @over?]]
       [:div (make-panel-attrs "rc-h-split-bottom" @dragging? (- 100 @split-perc))
        panel-2]])))
