(ns leihs-system.doo-runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [leihs-system.core-test]))

(doo-tests 'leihs-system.core-test)
