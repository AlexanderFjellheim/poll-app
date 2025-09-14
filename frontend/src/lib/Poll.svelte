<script>
    let pollId = 42;
    let question = "Pineapple on pizza: Is it acceptable?";
    let options = [
        { id: 1, text: "Yes", votes: 10 },
        { id: 2, text: "No", votes: 5 },
        { id: 3, text: "Only on weekends", votes: 1 }
    ];
</script>

<div>
    <p class="pollId">Poll#{pollId}</p>
    <h2 class="question">"{question}"</h2>
    <table>
        <thead>
            <tr>
<!--                <th>Options</th>-->
<!--                <th>Votes</th>-->
            </tr>
        </thead>
        <tbody>
            {#each options as option}
                <tr>
                    <td>{option.text}</td>
                    <!-- Ternary operator to handle singular/plural vote text -->
                    <td></td>
                    <td>{option.votes} {option.votes === 1 ? "Vote" : "Votes"}</td>
                </tr>
            {/each}
        </tbody>
    </table>
</div>


<style>
    div {
        border: 2px solid #ddd;
        border-radius: 5px;
        display: flex;
        flex-direction: column;
        align-items: center;
        width: auto;
    }
    .pollId {
        background-color: #486dff;
        color: white;
        align-self: flex-start;
        justify-self: flex-start;
        margin: 0;
        padding: 8px;
        border-radius: 5px;
        font-weight: bold;
    }

    .question {
        background-color: #fff4de;
        padding: 10px;
        border-radius: 5px;
        font-style: italic;
    }
    table {
        border-collapse: collapse;
        width: 75%;
        margin-bottom: 5%;
        margin-left: 5%;
    }

    th, td {
        border: 1px solid #ddd;
        padding: 8px;
    }

    th {
        background-color: #f2f2f2;
        text-align: left;
    }
</style>