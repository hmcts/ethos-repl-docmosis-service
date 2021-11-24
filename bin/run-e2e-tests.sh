#!/bin/bash
set -ex

export TESTS_FOR_ACCESSIBILITY='true'
export E2E_OUTPUT_DIR='./functional-output/'

yarn test:functional
