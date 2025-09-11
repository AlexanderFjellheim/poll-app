EX2

I originally used @JsonIgnore to prevent infinite recursion in my entity relationships, but I realized that using @JsonBackReference and @JsonManagedReference would be a better approach for handling bidirectional relationships in JSON serialization.
Like so:
Poll.java

    @JsonBackReference("user-polls")
    private User creator;

    @JsonManagedReference("poll-options")
    private List<VoteOption> options = new ArrayList<>();

User.java

    @JsonManagedReference("user-polls")
    private List<Poll> polls = new ArrayList<>();

    @JsonManagedReference("user-votes")
    private List<Vote> votes = new ArrayList<>();

Vote.java

    @JsonBackReference("user-votes")
    private User user;
    
    //@JsonManagedReference("option-votes")
    private VoteOption option;

VoteOption.java

    @JsonBackReference("poll-options")
    private Poll poll;
    
    //@JsonBackReference("option-votes")
    @JsonIgnore
    private List<Vote> votes = new ArrayList<>();

However, when I started to implement the application and writing tests, I encountered issues with the serialization of
the Poll and Vote entities. I needed to serialize Poll.creator and Vote.user so I moved away from using @JsonBackReferences.
And used **@JsonIdentityInfo** instead to break cycles without hiding the back-references.

What ended up working for me was to use @JsonIdentityInfo on the entities, and @JsonIgnore only on the lists of votes and polls.
Like so:

User.java

    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", scope = User.class)

