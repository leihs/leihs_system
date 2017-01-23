(ns ^:figwheel-no-load leihs.system.dev
  (:require [leihs.system.ui :as ui]
            [figwheel.client :as figwheel :include-macros true]))

(enable-console-print!)

(figwheel/watch-and-reload
  :websocket-url "ws://localhost:3229/figwheel-ws"
  :jsload-callback ui/mount-root)

(ui/init!)
