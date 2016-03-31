#!/bin/bash

rm -f resource/dc4cities.sqlt
sqlite3 -init resource/init.sql  resource/dc4cities.sqlt < resource/dc4cities_create.sql
