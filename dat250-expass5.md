# EX 5

I installed and ran valkey-server locally (with DNF on Fedora) and used valkey-cli to get familiar with redis and commands. 
PING/PONG worked, so I started trying out commands SET/GET and EXPIRE/TTL and understanding the usages. 
For the tasks I used a Set to track logged-in users (SADD, SREM, SMEMBERS, SCARD) and Hashes to represent a poll with options and vote counts. 
Each option hash had a caption and a votes field, and I updated counts with HINCRBY.

After that I expanded the Jedis example. It connected to the local server, did the same two experiments in code: the Set for logged-in users and the Hashes for poll options, and hincrBy.

Then I added a simple cache to the poll app. I added a GET /polls/{pollId}/counts endpoint. On a request it tries Redis first;
if there’s nothing, it counts from the in-memory PollManager (just sizes of option.getVotes()) and stores the result back in Redis with a short TTL. 
I changed the VoteController so when a user votes the cache is updated with HINCRBY: first vote does +1 on the new bucket, 
changing a vote does -1 on the old and +1 on the new, and if the user clicks the same option again it does nothing. 
When options are added/updated/deleted or a poll is deleted, I just invalidate the hash so the next read recomputes.

Previously the frontend Poll component derived counts on the client. Now I could use /polls/{id}/counts. The response shows counts by presentationOrder and after a vote it refreshes that endpoint.

I had a bug where re-voting the same option kept incrementing. The manager already returned the existing vote in that case but the controller still did a +1. 
I fixed it by skipping cache updates when old and new presentationOrder are the same. After that the Redis counts match the in-memory state.
In the process I found MONITOR to be very convenient for debugging and also the Intellij database connection 
(was a fast way to get an overview of all created with lists, sets, hash tables etc without having to type multiple commands in the cli)