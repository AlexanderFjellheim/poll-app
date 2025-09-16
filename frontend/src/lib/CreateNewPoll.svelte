<script>
    import { getContext, createEventDispatcher } from 'svelte';

    // Get reactive session from context
    const { apiBase, selectedUser } = getContext('session');
    const dispatch = createEventDispatcher();

    let question = '';
    let validUntil = toLocalDateTime(new Date(Date.now() + 7 * 24 * 3600 * 1000));
    let options = [
        { caption: '', presentationOrder: 1 },
        { caption: '', presentationOrder: 2 }
    ];
    let saving = false;

    function toLocalDateTime(d) {
        const pad = (n) => String(n).padStart(2, '0');
        return `${d.getFullYear()}-${pad(d.getMonth()+1)}-${pad(d.getDate())}T${pad(d.getHours())}:${pad(d.getMinutes())}`;
    }

    function addOption() {
        options = [...options, { caption: '', presentationOrder: options.length + 1 }];
    }

    function removeOption(i) {
        options = options.filter((_, idx) => idx !== i)
            .map((o, idx) => ({ ...o, presentationOrder: idx + 1 }));
    }

    async function submit() {
        if (!$selectedUser) {
            alert('User must be selected before creating a poll.');
            return;
        }
        const trimmed = question.trim();
        if (!trimmed) {
            alert('Please enter a question.');
            return;
        }
        const cleanOptions = options
            .map((o, idx) => ({ caption: o.caption.trim(), presentationOrder: idx + 1 }))
            .filter((o) => o.caption);

        if (cleanOptions.length < 2) {
            alert('Please provide at least two non-empty options.');
            return;
        }

        saving = true;
        try {
            const body = {
                question: trimmed,
                // Manager will set publishedAt if null; include validUntil from the picker
                validUntil: validUntil ? new Date(validUntil).toISOString() : null,
                options: cleanOptions
            };

            const res = await fetch(`${apiBase}/polls?userId=${$selectedUser.id}`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(body)
            });

            if (!res.ok) {
                const txt = await res.text();
                throw new Error(txt || `HTTP ${res.status}`);
            }

            const created = await res.json();
            // Let parent refresh polls
            dispatch('created', { poll: created });

            // Reset form
            question = '';
            options = [
                { caption: '', presentationOrder: 1 },
                { caption: '', presentationOrder: 2 }
            ];
        } catch (e) {
            console.error(e);
            alert('Failed to create poll: ' + e.message);
        } finally {
            saving = false;
        }
    }
</script>

<form on:submit|preventDefault={submit} class="create-poll">
    <h3>Create new poll</h3>

    <label>
        Question
        <input
                type="text"
                bind:value={question}
                placeholder="Favorite color?" />
    </label>

    <label>
        Deadline
        <input
                type="datetime-local"
                bind:value={validUntil} />
    </label>

    <fieldset>
        <legend>Options</legend>
        {#each options as o, i (i)}
            <div class="option-row">
                <input
                        type="text"
                        bind:value={o.caption}
                        placeholder={`Option ${i + 1}`} />
                {#if options.length > 2}
                    <button type="button" class="ghost" on:click={() => removeOption(i)}>Remove</button>
                {/if}
            </div>
        {/each}
        <button type="button" on:click={addOption}>+ Add option</button>
    </fieldset>

    <div class="actions">
        <button type="submit" disabled={!$selectedUser || saving}>
            {#if saving}Creating…{/if}
            {#if !$selectedUser}Select a user to create{/if}
            {#if $selectedUser && !saving}Create poll{/if}
        </button>
    </div>
</form>

<style>
    .create-poll { display: grid; gap: .75rem; max-width: 560px; }
    label { display: grid; gap: .25rem; }
    input[type="text"], input[type="datetime-local"] {
        padding: .5rem .6rem; border: 1px solid #ccc; border-radius: .5rem;
    }
    fieldset { border: 1px solid #eee; border-radius: .5rem; padding: .75rem; }
    legend { padding: 0 .25rem; }
    .option-row { display: flex; gap: .5rem; align-items: center; margin-bottom: .5rem; }
    .ghost { background: transparent; border: 1px solid #ddd; border-radius: .5rem; padding: .3rem .6rem; }
    .actions { margin-top: .5rem; }
    button[type="submit"][disabled] { opacity: .6; cursor: not-allowed; }
</style>
