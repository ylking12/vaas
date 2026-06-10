
const options = (data,valType) => {
    return {
        backgroundColor: '#000000',
        tooltip: {
            trigger: 'axis',
            axisPointer: {
                type: 'shadow',
            },
            formatter:function(params){

                return '时间: '+params[0].name+'<br>'+valType+': '+params[0].value
            }
        },
        grid: {
            right: 30,
            top: 20,
            bottom: 40,
            left: 40
        },
        xAxis: {
            type: 'category',
            /*data: data.map(item => item.time === "0h" ? "now" : item.time),*/
            data: data.map((item, index) => {
                return index === data.length - 1 ? 'now' : item.time;
            }),
            axisTick: {
                show: false
            },
            splitLine: {
                show: false
            },
            axisLine: {
                lineStyle: {
                    color: '#FFF6DA'/*'#b2dcfb'*/
                }
            },
            axisLabel: {
                /*interval: 5,*/
                interval: 0, // 必须设为 0 否则 formatter 不生效
                formatter: function (value) {
                    console.log(value)
                    const showLabels = ['-23h', '-18h', '-12h', '-6h', 'now'];
                    return showLabels.includes(value) ? value : '';
                },
                margin: 15,
                fontSize: 18
            },
            nameTextStyle: {
                padding: [10, 100, -30, -30]
            }
        },
        yAxis: {

            type: 'value',
            splitNumber:4,
            axisTick: {
                show: false
            },
            splitLine: {
                show: true,
                lineStyle: {
                    type: 'dotted',       // 点状线
                    color: 'rgba(255,255,255,0.2)' // 半透明白色，可根据需要调整
                }
            },
            axisLine: {
                show:true,
                lineStyle: {
                    color: '#FFF6DA'/*'#b2dcfb'*/
                }
            },
            axisLabel: {
                fontSize: 18
            },
            nameTextStyle: {
                color: '#ffffff',
                fontSize: 16,
                padding: [0, -100, -20, 10]
            }
        },
        series: [/*{
            type: 'bar',
            data: data.map(item => item.value || 0),
            barWidth: 18,
            itemStyle: {
                color: '#FFF6DA'
            }
        }, */{
            type: 'line',
            hoverAnimation:true,
            legendHoverLink:true,
            data: data.map(item => item.value || 0),
            symbol: "none",
            itemStyle: {
                color: '#68C4D0'
            }
        }]
    }
}

export {options}
