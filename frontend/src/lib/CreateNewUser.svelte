<script>
    import { createEventDispatcher } from "svelte";
    import {API_URL} from "./config.js";

    let newUserUsername = "";
    let newUserEmail = "";

    const dispatch = createEventDispatcher();

    async function createUser() {
        if (newUserUsername.trim() === "" || newUserEmail.trim() === "") {
            alert("Username and Email cannot be empty.");
            return;
        }

        try {
            const response = await fetch(API_URL+"/users", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({
                    username: newUserUsername,
                    email: newUserEmail,
                }),
            });

            if (response.ok) {
                alert("New user created!");
                dispatch("userCreated"); // Notify parent to refresh users
            } else {
                alert("Failed to create user.");
            }
        } catch (error) {
            alert(`Error: ${error.message}`);
        }
    }
</script>

<div class="create-user-form">
    <h2>Create New User</h2>
    <label for="name">Username:</label>
    <input id="name" type="text" bind:value={newUserUsername} />
    <label for="email">Email:</label>
    <input id="email" type="email" bind:value={newUserEmail} />
    <button on:click={createUser}>Create User</button>
</div>