<script>
  import { setContext, onMount} from "svelte";
  import { writable} from "svelte/store";

  import CreateNewUser from "./lib/CreateNewUser.svelte";
  import Poll from "./lib/Poll.svelte";
  import CreateNewPoll from "./lib/CreateNewPoll.svelte";

  const apiBase = 'http://localhost:8080'
  const selectedUser = writable(null) // ★ reactive
  setContext('session', { apiBase, selectedUser }) // ★ provide the store

  let users = [];
  let polls = [];

  async function loadUsers() {
    const r = await fetch(`${apiBase}/users`)
    users = await r.json()
  }
  async function loadPolls() {
    const r = await fetch(`${apiBase}/polls`)
    polls = await r.json()
  }
  onMount(() => { loadUsers(); loadPolls() })

  function pickUser(e) {
    const id = Number(e.target.value)
    selectedUser.set(users.find(u => u.id === id) ?? null)
  }
  function onPollDeleted(e) {
    const id = e.detail.id
    polls = polls.filter(p => p.id !== id)
  }
</script>

<main>
  <h1>Hello</h1>

  <label for="users">Choose a user:</label>
  <select on:change={pickUser}>
    <option value="">— select —</option>
    {#each users as u}
      <option value={u.id}>{u.username}</option>
    {/each}
  </select>

    <CreateNewUser on:userCreated={loadUsers}/>

    {#if $selectedUser}
      <CreateNewPoll on:created={() => loadPolls()} />
    {/if}

  <h2>Available Polls:</h2>
  {#each polls as p (p.id)}
    <Poll {p} on:deleted={onPollDeleted} />
  {/each}

</main>