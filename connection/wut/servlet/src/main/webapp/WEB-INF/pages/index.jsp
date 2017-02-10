<!DOCTYPE html>
<html>
<head>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<link rel="stylesheet" type="text/css" media="screen"
	href="./Beispiel:SELFHTML-Beispiel-Grundlayout.css" />
<title>display:table</title>
<style>
div.table {
	display: table;
	border-collapse: collapse;
}

div.tr {
	display: table-row;
}

div.td {
	display: table-cell;
	border: thin solid green;
	padding: 5px;
}
</style>
</head>
<body>
	<h1>WUT simulator servlet</h1>

	<h2>Values overview:</h2>
	<main>
	<div class="table">
	<div class="tr">
            <div class="td">Index:</div>
            <div class="td">0</div>
            <div class="td">1</div>
            <div class="td">2</div>
            <div class="td">3</div>
            <div class="td">4</div>
            <div class="td">5</div>
            <div class="td">6</div>
            <div class="td">7</div>
            <div class="td">8</div>
            <div class="td">9</div>
            <div class="td">10</div>
            <div class="td">11</div>
            <div class="td">12</div>
            <div class="td">13</div>
            <div class="td">14</div>
            <div class="td">15</div>
        </div>
		<div class="tr">
			<div class="td">Outputs:</div>
			<div class="td">${output0}</div>
			<div class="td">${output1}</div>
			<div class="td">${output2}</div>
			<div class="td">${output3}</div>
			<div class="td">${output4}</div>
			<div class="td">${output5}</div>
			<div class="td">${output6}</div>
			<div class="td">${output7}</div>
			<div class="td">${output8}</div>
			<div class="td">${output9}</div>
			<div class="td">${output10}</div>
			<div class="td">${output11}</div>
			<div class="td">${output12}</div>
			<div class="td">${output13}</div>
			<div class="td">${output14}</div>
			<div class="td">${output15}</div>
		</div>
		<div class="tr">
            <div class="td">Inputs:</div>
            <div class="td">${input0}</div>
            <div class="td">${input1}</div>
            <div class="td">${input2}</div>
            <div class="td">${input3}</div>
            <div class="td">${input4}</div>
            <div class="td">${input5}</div>
            <div class="td">${input6}</div>
            <div class="td">${input7}</div>
            <div class="td">${input8}</div>
            <div class="td">${input9}</div>
            <div class="td">${input10}</div>
            <div class="td">${input11}</div>
            <div class="td">${input12}</div>
            <div class="td">${input13}</div>
            <div class="td">${input14}</div>
            <div class="td">${input15}</div>            
        </div>
        <div class="tr">
            <div class="td">Counter:</div>
            <div class="td">${counter0}</div>
            <div class="td">${counter1}</div>
            <div class="td">${counter2}</div>
            <div class="td">${counter3}</div>
            <div class="td">${counter4}</div>
            <div class="td">${counter5}</div>
            <div class="td">${counter6}</div>
            <div class="td">${counter7}</div>
            <div class="td">${counter8}</div>
            <div class="td">${counter9}</div>
            <div class="td">${counter10}</div>
            <div class="td">${counter11}</div>
            <div class="td">${counter12}</div>
            <div class="td">${counter13}</div>
            <div class="td">${counter14}</div>
            <div class="td">${counter15}</div>
        </div>
	</div>
	</main>
	<h3>Request : ${request}</h3>
	<h3>Response : ${response}</h3>
</body>
</html>