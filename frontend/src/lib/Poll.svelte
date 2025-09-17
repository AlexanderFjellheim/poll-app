<script>
    import {createEventDispatcher, getContext, onMount} from "svelte";
    import {API_URL} from "./config.js";
    export let p; // receive the poll prop

    const { selectedUser } = getContext('session') // ★ get the store
    let counts = new Map() // optionId -> number

    async function loadCount(optionId) {
        // Preferred nested endpoint:
        const res = await fetch(`${API_URL}/polls/${p.id}/options/${optionId}/votes`)
        if (res.ok) {
            const list = await res.json()
            counts.set(optionId, list.length)
            counts = new Map(counts) // trigger update
            return
        }
        // Fallback: fetch all votes and filter client-side
        const all = await (await fetch(`${API_URL}/votes`)).json()
        const n = all.filter(v => v.option?.id === optionId).length
        counts.set(optionId, n)
        counts = new Map(counts)
    }

    async function loadAllCounts() {
        await Promise.all(p.options.map(o => loadCount(o.id)))
    }

    const dispatch = createEventDispatcher()
    onMount(loadAllCounts)

    async function vote(optionId) {
        const user = $selectedUser
        if (!user) return
        await fetch(`${API_URL}/votes?userId=${user.id}&optionId=${optionId}`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: '{}' // ensure JSON content type
        })
        // refresh only this option + the other options in same poll if your backend replaces previous vote
        await Promise.all(p.options.map(o => loadCount(o.id)))
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
                    {#if $selectedUser}
                        <button on:click={() => vote(o.id)}>Vote as {$selectedUser.username}</button>
                    {/if}
                </td>
                <td>
                    {counts.get(o.id)} {counts.get(o.id) === 1 ? "Vote" : "Votes"}
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
    th, td {
        border: 1px solid #ddd;
        padding: 8px;
    }
    th {
        background-color: #f2f2f2;
        text-align: left;
    }

</style>