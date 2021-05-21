import pandas as pd
import mysql.connector
import plotly.graph_objects as go
from plotly.subplots import make_subplots

db = mysql.connector.connect(
	host="localhost",
	user="root",
	password="Johnmyung@06",
	database="corona",
	)

cur = db.cursor()
sql_query="SELECT * FROM corona"
cur.execute(sql_query)
df = pd.DataFrame(cur.fetchall())
df.columns=['id','active','x','place','confirmed','y','update_time','recovered','w','deaths','latitude','longitude','country','province']
df.drop(columns=['x','y','w'],axis=1, inplace=True)


df_total = df.groupby("country", as_index=False).agg(
    {
        "confirmed" : "sum",
        "active" : "sum",
        "recovered" : "sum",
        "deaths": "sum"
    }
)

total_confirmed = df["confirmed"].sum()
total_recovered = df["recovered"].sum()
total_active = df["active"].sum()
total_deaths = df["deaths"].sum()

df_top10 = df_total.nlargest(10, "confirmed")
top10_countries_1 = df_top10["country"].tolist()
top10_confirmed = df_top10["confirmed"].tolist()

df_top10 = df_total.nlargest(10, "recovered")
top10_countries_2 = df_top10["country"].tolist()
top10_recovered = df_top10["recovered"].tolist()

df_top10 = df_total.nlargest(10, "active")
top10_countries_3 = df_top10["country"].tolist()
top10_active = df_top10["active"].tolist()

df_top10 = df_total.nlargest(10, "deaths")
top10_countries_4 = df_top10["country"].tolist()
top10_deaths = df_top10["deaths"].tolist()


fig = make_subplots(
    rows = 5, cols = 7,
    specs=[
            [{"type": "scattergeo", "rowspan": 4, "colspan": 3}, None, None, {"type": "indicator"}, {"type": "indicator"}, {"type": "indicator"},{"type": "indicator"} ],
            [    None, None, None,               {"type": "bar", "colspan":3}, None, None,None],
            [    None, None, None,              {"type": "bar", "colspan":3}, None, None,None],
            [    None, None, None,               {"type": "bar", "colspan":3}, None, None,None],
            [    None, None, None,               {"type": "bar", "colspan":3}, None, None,None],
          ]
)

message = df["country"] +" "+ df["province"] + "<br>"
message += "Confirmed: " + df["confirmed"].astype(str) + "<br>"
message += "Deaths: " + df["deaths"].astype(str) + "<br>"
message += "Active: " + df["active"].astype(str) + "<br>"
message += "Recovered: " + df["recovered"].astype(str) + "<br>"
message += "Last updated: " + df["update_time"].astype(str)
df["text"] = message

fig.data=[]
fig.add_trace(
    go.Scattergeo(
        locationmode = "country names",
        lon = df["longitude"],
        lat = df["latitude"],
        hovertext = df["text"],
        showlegend=False,
        marker = dict(
            size = 10,
            opacity = 0.8,
            reversescale = True,
            autocolorscale = True,
            symbol = 'square',
            line = dict(
                width=1,
                color='rgba(102, 102, 102)'
            ),
            cmin = 0,
            color = df['confirmed'],
            cmax = df['confirmed'].max(),
            colorbar_title="Confirmed Cases<br>Latest Update",  
            colorbar_x = -0.05
        )

    ),
    
    row=1, col=1
)



fig.add_trace(
    go.Indicator(
        mode="number",
        value=total_confirmed,
        title="Confirmed Cases",
    ),
    row=1, col=4
)

fig.add_trace(
    go.Indicator(
        mode="number",
        value=total_recovered,
        title="Recovered Cases",
    ),
    row=1, col=5
)

fig.add_trace(
    go.Indicator(
        mode="number",
        value=total_deaths,
        title="Deaths Cases",
    ),
    row=1, col=6
)

fig.add_trace(
    go.Indicator(
        mode="number",
        value=total_active,
        title="Active Cases",
    ),
    row=1, col=7
)



fig.add_trace(
    go.Bar(
        x=top10_countries_1,
        y=top10_confirmed, 
        name= "Confirmed Cases",
        marker=dict(color="Yellow"), 
        showlegend=True,
    ),
    row=2, col=4
)

fig.add_trace(
    go.Bar(
        x=top10_countries_2,
        y=top10_recovered, 
        name= "Recovered Cases",
        marker=dict(color="Green"), 
        showlegend=True),
    row=3, col=4
)

fig.add_trace(
    go.Bar(
        x=top10_countries_4,
        y=top10_deaths, 
        name= "Deaths Cases",
        marker=dict(color="crimson"), 
        showlegend=True),
    row=4, col=4
)

fig.add_trace(
    go.Bar(
        x=top10_countries_3,
        y=top10_active, 
        name= "Active Cases",
        marker=dict(color="cyan"), 
        showlegend=True),
    row=5, col=4
)


fig.update_layout(
    template="seaborn",
    title = "Global COVID-19 Cases (Last Updated: " + str(df["update_time"][0]) + ")",
    showlegend=True,
    legend_orientation="h",
    legend=dict(x=0.65, y=0.8),
    geo = dict(
            projection_type="orthographic",
            showcoastlines=True,
            landcolor="green", 
            showland= True,
            showocean = True,
            lakecolor="LightBlue"
    ),

    annotations=[
        dict(
            text="Source: https://bit.ly/3aEzxjK",
            showarrow=False,
            xref="paper",
            yref="paper",
            x=0.35,
            y=0)
    ]
)

fig.write_html('/Users/jithinjose/Downloads/Corona-Tracker/src/main/resources/static/plot/visualization_plot.html')
