// ================= Sidebar =================
document.addEventListener("DOMContentLoaded", function () {
    const sidebar = document.querySelector(".sidebar");
    const content = document.querySelector(".content");
    const openBtn = document.querySelector(".fa-bars");
    const closeBtn = document.querySelector(".crossBtn");

    if (sidebar && content && openBtn && closeBtn) {
        openBtn.addEventListener("click", () => {
            sidebar.style.display = "block";
            content.style.marginLeft = "20%";
        });

        closeBtn.addEventListener("click", () => {
            sidebar.style.display = "none";
            content.style.marginLeft = "0%";
        });
    }
});

// ================= Eye & Hand Animation =================
document.addEventListener("DOMContentLoaded", function () {
    const usernameRef = document.getElementById("username");
    const passwordRef = document.getElementById("password");
    const eyeL = document.querySelector(".eyeball-l");
    const eyeR = document.querySelector(".eyeball-r");
    const handL = document.querySelector(".hand-l");
    const handR = document.querySelector(".hand-r");

    if (usernameRef && passwordRef && eyeL && eyeR && handL && handR) {
        const normalEyeStyle = () => {
            eyeL.style.cssText = `left:0.6em; top:0.6em;`;
            eyeR.style.cssText = `right:0.6em; top:0.6em;`;
        };

        const normalHandStyle = () => {
            handL.style.cssText = `height: 2.81em; top:8.4em; left:7.5em; transform: rotate(0deg);`;
            handR.style.cssText = `height: 2.81em; top:8.4em; right:7.5em; transform: rotate(0deg);`;
        };

        usernameRef.addEventListener("focus", () => {
            eyeL.style.cssText = `left: 0.75em; top: 1.12em;`;
            eyeR.style.cssText = `right: 0.75em; top: 1.12em;`;
            normalHandStyle();
        });

        passwordRef.addEventListener("focus", () => {
            handL.style.cssText = `height: 6.56em; top: 3.87em; left: 11.75em; transform: rotate(-155deg);`;
            handR.style.cssText = `height: 6.56em; top: 3.87em; right: 11.75em; transform: rotate(155deg);`;
            normalEyeStyle();
        });

        document.addEventListener("click", (e) => {
            if (e.target !== usernameRef && e.target !== passwordRef) {
                normalEyeStyle();
                normalHandStyle();
            }
        });
    }
});

// ================= Search Function =================
document.addEventListener("DOMContentLoaded", function () {
    const searchInput = document.getElementById("search-result");
    const resultDiv = document.querySelector(".search-result");

    if (!searchInput || !resultDiv) return;

    // Function to fetch and display search results
    function performSearch() {
        const query = searchInput.value.trim();

        if (!query) {
            resultDiv.style.display = "none";
            resultDiv.innerHTML = '';
            return;
        }

        const url = `http://localhost:8182/search/${query}`;

        fetch(url)
            .then(response => response.json())
            .then(data => {
                if (!data || data.length === 0) {
                    resultDiv.innerHTML = '<div class="list-group-item">No contacts found</div>';
                    resultDiv.style.display = "block";
                    return;
                }

                let html = '<div class="list-group">';
                data.forEach(contact => {
					//ye anme show kr rha tha 
                   /// html += `<a href="/user/${contact.cId}/contact" class="list-group-item list-group-action">${contact.name}</a>`;
				   
				  
				 
				   
				    html += `<a href="/user/${contact.cId}/contact" class="list-group-item list-group-action">
				               ${contact.name} ${contact.secondName ? '(' + contact.secondName + ')' : ''}
				            </a>`;
                });
                html += '</div>';

                resultDiv.innerHTML = html;
                resultDiv.style.display = "block";
            })
            .catch(error => console.error("Search Error:", error));
    }

    // Trigger search on keyup
    searchInput.addEventListener("keyup", performSearch);

    // Click outside to hide results
    document.addEventListener("click", function(e) {
        if (!searchInput.contains(e.target) && !resultDiv.contains(e.target)) {
            resultDiv.style.display = "none";
        }
    });
});
