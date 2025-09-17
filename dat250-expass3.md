EX 3

I have implemented a frontend for the poll API using Svelte 5.
I created a Poll component and components to create polls and users.

Biggest challenges were keeping the page dynamic, accessing the selected user and context, react to selects without having to do a page refresh.

Creating users, polls, voting on options and switching users is implemented.

To implement vote counts, I created a nested mapping in VoteOptions to get votes by voteOptionID.

TODO: Fix responses in backend for all CRUD operations, ex: 200 instead of 201 on created etc...
TODO: Fix an issue where the first user to create a poll creates a poll with creator value of userID, but not the actual USER object.
TODO: Specify CORS rules (currently allows all)