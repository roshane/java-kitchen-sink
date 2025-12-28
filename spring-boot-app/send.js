#!/usr/bin/env node

const crypto = require('crypto')
const ITERATIONS = 1000;
const URL = 'http://localhost:8080';

for (let i = 1; i <= ITERATIONS; i++) {
    let id = crypto.randomUUID()
    console.log(new Date(), 'send:', id);
    let requestPayload = {
        id: id,
        message: `${i} message`
    }
    fetch(URL, {
        method: 'POST',
        body: JSON.stringify(requestPayload),
        headers: {
            'Content-Type': 'application/json'
        }
    })
        .then(resp => resp.json())
        .then(resp => {
            console.log(new Date(), resp);
        })
}
