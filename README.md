# dbinstaller
![Build status](https://travis-ci.org/scf37/dbinstaller.svg?branch=master)
[![Coverage Status](https://coveralls.io/repos/github/scf37/dbinstaller/badge.svg?branch=master)](https://coveralls.io/github/scf37/dbinstaller?branch=master)

A tool for managing DDL and DML scripts for databases. Proper usage ensures that:
- scripts are always applied in order they are written
- scripts will not be applied twice
- modified script will be applied again only if it is re-entrant

##usage
0. have JRE8 on your system
1. wget https://raw.githubusercontent.com/scf37/dbinstaller/master/dbi && chmod +x dbi
2. ./dbi
3. read command-line help and hack away

##.lst file language

```
#this is a comment

#apply this script if it is not applied yet
#if modified, apply it agaig
folder/2016-02-05-do_something.sql

#apply this script if it is not applied yet
#if modified, abort execution
once folder/2016-02-07-do_more.sql

#always run this script
always folder/2016-02-11-mainterance.sql

#include another .lst file
include another_lst_file.lst
```