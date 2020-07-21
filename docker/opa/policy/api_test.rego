package http.authz

import input

default allow = false

allow {
    input.method == "GET"
    input.path = ["rest","test"]
}

allow {
    input.method == "GET"
    input.path = ["rest","rest","test2"]
}