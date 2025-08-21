/*
//side bar start
document.addEventListener("DOMContentLoaded", function () {
    const sidebar = document.querySelector(".sidebar");
    const content = document.querySelector(".content");
    const openBtn = document.querySelector(".fa-bars");
    const closeBtn = document.querySelector(".crossBtn");

    // Open sidebar
    openBtn.addEventListener("click", function () {
        sidebar.style.display = "block";
        content.style.marginLeft = "20%"; // space for sidebar
    });

    // Close sidebar
    closeBtn.addEventListener("click", function () {
        sidebar.style.display = "none";
        content.style.marginLeft = "0%"; // full width
    });
});

//side bar end




let usernameRef = document.getElementById("username");
let passwordRef = document.getElementById("password");
let eyeL = document.querySelector(".eyeball-l");
let eyeR = document.querySelector(".eyeball-r");
let handL = document.querySelector(".hand-l");
let handR = document.querySelector(".hand-r");

let normalEyeStyle = () => {
  eyeL.style.cssText = `left:0.6em; top:0.6em;`;
  eyeR.style.cssText = `right:0.6em; top:0.6em;`;
};

let normalHandStyle = () => {
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
});*/



/*
const search=()=>{
	let query =$("#search-input").val();
	
	if(query==''){
		$(".search-result").hide();
	}else{
		console.log(query);
		
		let url=` http://localhost:8182/search/${query}`;
		
		fetch(url)
		       .then((response)=>{
				return response.json();
			   })
			   .then((data) =>{
				   
				let text =`<div class='list-group'>`;
				data.forEach((contact) =>{
					text+=`<a href='#' class=' list-group-item  list-group-action ' > ${contact.name} </a>`
				})
				
				text +=`</div>`;
				
				$(".search-result").html(text);
				$(".search-result").show();
			   });
		
		
		
	}
};*/
/*

function search() {
    let query = $("#search-result").val();
    if(query == '') {
        $(".search-result").hide();
    } else {
        let url = `http://localhost:8182/search/${query}`;
        fetch(url)
        .then(response => response.json())
        .then(data => {
            let text = `<div class='list-group'>`;
            data.forEach(contact => {
                text += `<a href='/user/${contact.cId}/contact' class='list-group-item list-group-action'>${contact.name}</a>`;
            });
            text += `</div>`;
            $(".search-result").html(text).show();
        })
        .catch(error => console.error("Error:", error));
    }
}*/


