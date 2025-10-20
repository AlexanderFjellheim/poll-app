<script>
    import {createEventDispatcher, getContext, onMount} from "svelte";
    import {API_URL} from "./config.js";
    export let p; // receive the poll prop

    const { selectedUser } = getContext('session');
    let counts = {};

    async function loadAllCounts() {
        if (!p?.id) return;
        const res = await fetch(`${API_URL}/polls/${p.id}/counts`, {
            headers: { 'Accept': 'application/json' }
        });
        if (res.ok) {
            counts = await res.json();
        } else {
            counts = {};
        }
    }


    const dispatch = createEventDispatcher()
    onMount(loadAllCounts)

    async function vote(optionId) {
        const user = $selectedUser
        if (!user) {
            await fetch(`${API_URL}/votes?optionId=${optionId}`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: '{}'
            })
        }
        else {
            await fetch(`${API_URL}/votes?userId=${user.id}&optionId=${optionId}`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: '{}'
            })
        }

        // After voting, reload all counts
        await loadAllCounts();
    }

    let deleting = false;


    async function deletePoll() {
        deleting = true;
        try {
            const res = await fetch(`${API_URL}/polls/${p.id}`, {
                method: 'DELETE'
            });
            if (!res.ok) {
                const txt = await res.text();
                throw new Error(txt || `HTTP ${res.status}`);
            }
            // Tell parent to remove this poll from its list
            dispatch('deleted', { id: p.id });
        } catch (err) {
            console.error(err);
            alert('Failed to delete poll: ' + err.message);
        } finally {
            deleting = false;
        }
    }
</script>


<div>
    <p class="pollId">Poll#{p.id}</p>
    <!--{#if p.creator.id === $selectedUser.id}-->
    <button class="delete" on:click={deletePoll} disabled={deleting}>
        {deleting ? 'Deleting…' : 'Delete'}
    </button>
    <!--{/if}-->
    <h2 class="question">"{p.question}"</h2>
    <table>
        <thead>
        <tr></tr>
        </thead>
        <tbody>
        {#each p.options as o (o.id)}
            <tr>
                <td>{o.caption}</td>
                <td>
                    <!--{#if $selectedUser}-->
                    <!--    <button on:click={() => vote(o.id)}>Vote as {$selectedUser.username}</button>-->
                    <!--{/if}-->
                    <button on:click={() => vote(o.id)}>
                        {$selectedUser ? `Vote as ${$selectedUser.username}` : 'Vote anonymously'}
                    </button>
                </td>
                <td>
                    {(counts?.[o.presentationOrder] ?? 0)} {(counts?.[o.presentationOrder] ?? 0) === 1 ? "Vote" : "Votes"}
                </td>
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
    .delete {
        background: #ff4848;
        color: white;
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
    /*th,*/ td {
        border: 1px solid #ddd;
        padding: 8px;
    }
    /*th {*/
    /*    background-color: #f2f2f2;*/
    /*    text-align: left;*/
    /*}*/

</style>