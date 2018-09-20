### Small PoC as illustration for the eternal discussion DROP vs. REJECT.

This trying to answer a question "Which rules (`DROP` or `REJECT`) should be used in firewalls, IDS etc".

```bash
# insert several rules:
?sudo? iptables -I INPUT -p tcp --dport 881 -j DROP
?sudo? iptables -I INPUT -p tcp --dport 882 -j REJECT
?sudo? iptables -I INPUT -p tcp --dport 883 -j REJECT --reject-with icmp-port-unreachable
?sudo? iptables -I INPUT -p tcp --dport 884 -j REJECT --reject-with tcp-reset
?sudo? iptables -L INPUT | grep -E ':88[1-5](\s|$)' | tac; # + control no rule for 885

# do the scan:
nmap --reason `hostname` -p881-885

# remove rules:
?sudo? iptables -D INPUT -p tcp --dport 881 -j DROP
?sudo? iptables -D INPUT -p tcp --dport 882 -j REJECT
?sudo? iptables -D INPUT -p tcp --dport 883 -j REJECT --reject-with icmp-port-unreachable
?sudo? iptables -D INPUT -p tcp --dport 884 -j REJECT --reject-with tcp-reset
?sudo? iptables -L INPUT | grep -E ':88[1-5](\s|$)' | tac; # + control rules are deleted
```
results in following temporary rules in INPUT chain of iptables (reversed here because of `-I`):
```
DROP       tcp  --  anywhere             anywhere             tcp dpt:881
REJECT     tcp  --  anywhere             anywhere             tcp dpt:882 reject-with icmp-port-unreachable
REJECT     tcp  --  anywhere             anywhere             tcp dpt:883 reject-with icmp-port-unreachable
REJECT     tcp  --  anywhere             anywhere             tcp dpt:884 reject-with tcp-reset
```
and following output of nmap-scan process ("diff" used here for highliting purposes only):
```diff
  PORT    STATE    SERVICE REASON
- 881/tcp filtered unknown no-response    # - DROP
- 882/tcp filtered unknown no-response    # - REJECT
- 883/tcp filtered unknown no-response    # - REJECT --reject-with icmp-port-unreachable
+ 884/tcp closed   unknown conn-refused   # - REJECT --reject-with tcp-reset
+ 885/tcp closed   unknown conn-refused   # - NO LISTENER/SERVICE (really closed port)
```

Normally the reason to use `REJECT` is - it has to behave like a closed port, so should give the
intruder/port scanner a "message" - the port is just not here (has no listener, so looks like no service,
because of a default behavior of closed port without any firewall).

If one DROPs every port for an intruder (host appears to be down on his side), it seems to be fine also.
So in case of ban-actions of IDS where all ports are affected (like jail `recidive` of `fail2ban`), possibly 
the `DROP` should be a default choice.

But in normal case (e. g. multiport actions) one wants protect only some specific ports, therefore 
using `DROP` instead of `REJECT` one can actually giving the intruder more information, for example
that something like a firewall (IDS) is there, that could stimulate him to continue the "attack" 
in hope to provide required data at some point.

Well, corresponding the output above (green area) only `REJECT --reject-with tcp-reset` seems to behave as
a real closed port (884 = 885).
