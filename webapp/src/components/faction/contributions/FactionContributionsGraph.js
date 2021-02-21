import React, {useEffect, useState} from 'react';
import {ChartContainer, ChartRow, Charts, Legend, LineChart, styler, YAxis} from "react-timeseries-charts";
import {TimeSeries} from 'pondjs';
import {FaSpinner} from 'react-icons/fa';
import Grid from "@material-ui/core/Grid";


const style = styler([
    {key: "dexterity", color: "#393c5a", width: 1.5},
    {key: "strength", color: "#5a1000", width: 1.5},
    {key: "speed", color: "#005a06", width: 1.5},
    {key: "defence", color: "#585a0a", width: 1.5},
    {key: "total", color: "#5a0059", width: 2},
]);

function buildPoints(data) {
    let points = [];
    console.log(data);
    for (let i = 0; i < data['gymTotal'].length; i++) {
        points.push([data['gymTotal'][i].time, data['gymTotal'][i]['difference'], data['gymStrength'][i]['difference'],
            data['gymSpeed'][i]['difference'], data['gymDefence'][i]['difference'], data['gymDexterity'][i]['difference']]);
    }
    return points;
}

function buildTimeSeries(data) {
    return new TimeSeries({
        name: "Energy Used in Gym",
        columns: ["time", "total", "strength", "speed", "defence", "dexterity"],
        points: buildPoints(data)
    })
}

function FactionContributionsGraph(props) {
    const [isLoading, setIsLoading] = useState(true);
    const [data, setData] = useState(null);
    const {userId, selectedStartDate, selectedEndDate} = props;

    useEffect(() => {
        loadData();
    }, []);

    const loadData = () => {
        setIsLoading(true);
        fetch(`/api/faction/contributions/user/${userId}/${new Date(selectedStartDate).toISOString().slice(0, 16)}/${new Date(selectedEndDate).toISOString().slice(0, 16)}`)
            .then(res => res.json())
            .then(
                (result) => {
                    setIsLoading(false);
                    setData(buildTimeSeries(result));
                },
                (error) => {
                    setIsLoading(false);
                    setData(null);
                }
            )
    };

    return (
        <>
            {isLoading || data == null ? <FaSpinner icon="spinner" className="spinner"/> : (
                <Grid container justify="center">
                    <Grid item xs={7}>
                        <ChartContainer
                            title="Energy Spent in Gym Over Time"
                            titleStyle={{
                                fontWeight: 200,
                                fontSize: 20,
                                font: '"Goudy Bookletter 1911", sans-serif"',
                                fill: "#696969"
                            }}
                            timeRange={data.range()}
                            timeAxisStyle={{
                                ticks: {
                                    stroke: "#AAA",
                                    opacity: 0.25,
                                    "stroke-dasharray": "1,1"
                                },
                                values: {
                                    fill: "#AAA",
                                    "font-size": 12
                                }
                            }}
                            showGrid
                            maxTime={data.range().end()}
                            minTime={data.range().begin()}>
                            <ChartRow height="300">
                                <YAxis id="axis1" max={data.max("total")} label="Total Energy spent" width="50"
                                       type="linear"/>
                                <Charts>
                                    <LineChart axis="axis1" series={data}
                                               columns={["total", "strength", "speed", "defence", "dexterity"]}
                                               style={style}/>
                                </Charts>
                            </ChartRow>
                        </ChartContainer>
                    </Grid>
                    <Grid item xs={1}>
                        <Legend
                            type="line"
                            align="right"
                            stack={true}
                            style={style}
                            categories={[
                                {key: "total", label: "Total"},
                                {key: "strength", label: "Strength"},
                                {key: "speed", label: "Speed"},
                                {key: "defence", label: "Defence"},
                                {key: "dexterity", label: "Dexterity"},
                            ]}
                        />
                    </Grid>
                </Grid>
            )}
        </>
    );
}

export default FactionContributionsGraph;