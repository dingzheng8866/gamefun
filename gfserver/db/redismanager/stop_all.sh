#! /bin/bash                                                                                            
                                                                                                        
pids=$(ps -ef | grep redis-server | grep -v grep | awk '{print $2}')                                            
for p in $pids                                                                                          
do                                                                                                      
  echo "kill pid "$p                                                                                    
  kill -9 $p                                                                                            
done                                                                                                    
                                                                                                        
ps -ef | grep redis-server
