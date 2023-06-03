@echo off

rem wmic process where "commandline like '%%[D]service.id=1234567%%'" get processid, commandline /format:rawxml | more > X.xml

rem wmic process where "commandline like '%%[D]service.id=1234567%%'" get processid | more +1 > X.txt

rem "Find all Kappa processes"
wmic process where "commandline like '%%[c]om.kaizensundays.kappa.service.id.%%'" get | more > X.txt
