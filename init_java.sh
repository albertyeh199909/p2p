xterm -hold -title "Peer 1" -e "java p2p init 1 4 5 10" &

xterm -hold -title "Peer 4" -e "java p2p init 4 5 8 10" &

xterm -hold -title "Peer 5" -e "java p2p init 5 8 9 10" &

xterm -hold -title "Peer 8" -e "java p2p init 8 9 14 10" &

xterm -hold -title "Peer 9" -e "java p2p init 9 14 19 10" &

xterm -hold -title "Peer 14" -e "java p2p init 14 19 1 10" &

xterm -hold -title "Peer 19" -e "java p2p init 19 1 4 10" & 

#sleep 20s

#xterm -hold -title "Peer 15" -e "java p2p join 15 4 10" & 



