import React, { useCallback, useEffect, useState } from 'react'

import './dashboard.css'

import Chart from 'react-apexcharts'

import StatusCard from '../components/status-card/StatusCard'

import CardDiario from '../components/carddiario/cardDiario'

import ReceberMes from '../services/Mes/aReceberMes'

import CancelamentoMes from '../services/Mes/cancelamentoMes'

import Ativos from '../services/Mes/clientesAtivosMes'

import DevedoresMes from '../services/Mes/devedoresMes'

import { useSelector } from 'react-redux'

import Instalacoes from '../services/Dia/instalacoes'

import Cancelamentos from '../services/Dia/cancelamentos'

import RecebimentosRef from '../services/Dia/recebimentosRef'

import InstalacoesMes from '../services/Mes/instalacoesMes'

import { TextField } from '@material-ui/core'

import { Autocomplete } from '@material-ui/lab'

import Recebimentos from '../services/Dia/recebimentos'

import statusCardsContratos from '../assets/JsonData/status-card-data-contratos.json'

import cardDiario from '../assets/JsonData/card-diario.json'

import CircularProgress from '@material-ui/core/CircularProgress';

import { makeStyles } from '@material-ui/core/styles';

import FormControl from '@material-ui/core/FormControl';

import Select from '@material-ui/core/Select';

import axios from 'axios'

const Dashboard = () => {

    /* const FiltroMes = ['Janeiro', 'Fevereiro', 'Março', 'Abril', 'Maio', 'Junho', 'Julho', 'Agosto', 'Setembro', 'Outubro',
        'Novembro', 'Dezembro'];
 */
    const chartOptions = {



        series: [{
            name: 'Faturamento',
            data: [40, 70, 20, 90, 36, 80, 30, 91, 60]
        }
        ],
        options: {
            color: ['#2980b9', '#8e44ad'],
            chart: {
                background: 'transparent'
            },
            dataLabels: {
                enabled: false
            },
            stroke: {
                curve: 'smooth'
            },
            xaxis: {
                categories: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep']
            },
            legend: {
                position: 'top'
            },
            grid: {
                show: false
            }
        }
    }

    const useStyles = makeStyles((theme) => ({
        formControl: {
            margin: theme.spacing(1),
            minWidth: 120,

        },
        title: {
            color: "#fff"
        },
        select: {
            color: "#fff"
        },

        selectEmpty: {
            marginTop: theme.spacing(2),
        },
    }));
    const classes = useStyles();

    var current = new Date();

    const [dataContratos, setDataContratos] = useState([]);
    const [mesFiltroRecebimentos, setMesFiltroRecebimentos] = useState("00");
    const [dataMes, setDataMes] = useState("00");
    const [loading, setLoading] = useState(true);

    const [statusCardR, setStatusCardR] = useState([
        {
            "cor": "#019707",
            "icon": "bx bx-dollar-circle",
            "count": "0",
            "title": "Recebimentos"
        },
        {
            "cor": "#f02c35",
            "icon": "bx bx-dollar-circle",
            "count": "0",
            "title": "Devedores"
        },
        {
            "cor": "#f02c35",
            "icon": "bx bx-dollar-circle",
            "count": "0",
            "title": "A receber"
        }
    ]);

    const [dataRecebimentos, setDataRecebimentos] = useState([]);
    const [valueMes, setValueMes] = useState('');
    const [valueAno, setValueAno] = useState('');
    /*  const [inputValue, setInputValue] = useState(''); */

    const handleChangeMes = useCallback((event) => {
        setValueMes(event)
    }, [valueMes])

    const handleChangeAno = useCallback((event) => {
        setValueAno(event)
        console.log(event)
    }, [valueAno])
    const handleClick = useCallback(async () => {
        setLoading(true)


        var qtdReceberMes
        var qtdRecebimentoRef
        var qtdDevedoresMes

        const axiosrequest1 = await RecebimentosRef.getrecebimentosRef(valueMes + '/' + valueAno)
        const axiosrequest2 = await DevedoresMes.getdevedores(valueMes + '/' + valueAno)
        const axiosrequest3 = await ReceberMes.getreceber(valueMes + '/' + valueAno)
        // you could also use destructuring to have an array of responses
        await axios.all([axiosrequest1, axiosrequest2, axiosrequest3]).then(axios.spread(function (res1, res2, res3) {
            qtdRecebimentoRef = res1.data[0].Total
            qtdRecebimentoRef = parseFloat(qtdRecebimentoRef).toLocaleString(2).replace('.', ',')
            qtdDevedoresMes = res2.data[0].Quantidade
            qtdReceberMes = res3.data[0].Total

            statusCardR[0].count = qtdRecebimentoRef
            statusCardR[1].count = qtdDevedoresMes
            statusCardR[2].count = qtdReceberMes

            setStatusCardR(statusCardR)
            setDataRecebimentos(statusCardR)
            setLoading(false)


            console.log(valueAno)
            console.log(valueMes)
        }))
    }, [mesFiltroRecebimentos, valueAno,valueMes, dataMes, loading])




   /*  const teste = useCallback(async () => {

        console.log("chegouuuu")
        switch (valueAno + '/' + valueMes) {
            case "Janeiro":
                setDataMes(valueAno + '/' + valueMes)
                break;
            case "Fevereiro":
                setDataMes(valueAno + '/' + valueMes)
                break;
            case "Março":
                setDataMes(valueAno + '/' + valueMes)
                break;
            case "Abril":
                setDataMes(valueAno + '/' + valueMes)
                break;
            case "Maio":
                setDataMes(valueAno + '/' + valueMes)
                break;
            case "Junho":
                setDataMes(valueAno + '/' + valueMes)
                break;
            case "Julho":
                setDataMes(valueAno + '/' + valueMes)
                break;
            case "Agosto":
                setDataMes(valueAno + '/' + valueMes)
                break;
            case "Setembro":
                setDataMes(valueAno + '/' + valueMes)
                break;
            case "Outubro":
                setDataMes(valueAno + '/' + valueMes)
                break;
            case "Novembro":
                setDataMes(valueAno + '/' + valueMes)
                break;
            case "Dezembro":
                setDataMes(valueAno + '/' + valueMes)
                break;
        }

    }, [statusCardR, mesFiltroRecebimentos, loading, dataMes, valueAno, valueMes]) */


    useEffect(async () => {
        var qtdInstalacoes
        var qtdCancelamentos
        var qtdClientesAtivos
        var qtdRecebimentos
        var qtdInstalacoesMes
        var qtdCancelamentoMes

        //    setDataMes(`${current.toLocaleString('default', { month: 'long' })}/${current.getFullYear()}`)
        var date = `${current.toLocaleDateString('br-PT')}`;
        var dateMesReferencia = current.toLocaleDateString('br-PT').substring(3)
        var datePrimeiroDiaMes = `01${date.substring(2)}`

        await Instalacoes.getinstalacoes(date, date)
            .then((response) => {
                qtdInstalacoes = response.data[0].Quantidade
                console.log(qtdInstalacoes)
            })
            .catch((error) => {
                console.log(error);
            });

        await Cancelamentos.getcancelamentos(datePrimeiroDiaMes, date)
            .then((response) => {
                qtdCancelamentos = response.data[0].Quantidade
                console.log(qtdCancelamentos)
            })
            .catch((error) => {
                console.log(error);
            });
        await Recebimentos.getrecebimentos(date, date)
            .then((response) => {
                qtdRecebimentos = response.data[0].Total
                console.log(qtdRecebimentos)
                qtdRecebimentos = parseFloat(qtdRecebimentos).toLocaleString(2).replace('.', ',')
            })
            .catch((error) => {
                console.log(error);
            });
        await Ativos.getativos()
            .then((response) => {
                qtdClientesAtivos = response.data[0].Quantidade
                qtdClientesAtivos = parseFloat(qtdClientesAtivos).toFixed(2).replace('.', ',')
                console.log(qtdClientesAtivos)
            })
            .catch((error) => {
                console.log(error);
            });
        await InstalacoesMes.getinstalacoes(datePrimeiroDiaMes, date)
            .then((response) => {
                qtdInstalacoesMes = response.data[0].Quantidade
                console.log(qtdInstalacoesMes)
            })
            .catch((error) => {
                console.log(error);
            });
        await CancelamentoMes.getcancelamento(datePrimeiroDiaMes, date)
            .then((response) => {
                qtdCancelamentoMes = response.data[0].Quantidade
                console.log(qtdCancelamentoMes)
            })
            .catch((error) => {
                console.log(error);
            });

        cardDiario[0].count = qtdInstalacoes
        cardDiario[1].count = qtdCancelamentos
        cardDiario[2].count = qtdRecebimentos


        statusCardsContratos[0].count = qtdInstalacoesMes
        statusCardsContratos[1].count = qtdClientesAtivos
        statusCardsContratos[2].count = qtdCancelamentoMes




        setDataContratos(statusCardsContratos)
    }, [])


    const ThemeReducer = useSelector(state => state.ThemeReducer.mode)

    return (
        <div>
            <h2 className="page-header">Dashboard</h2>

            <h3 style={{ 'margin': '10px' }}>Hoje</h3>
            <div className="row">
                {
                    cardDiario.map((item, index) => (
                        <div className="col-4" key={index}>

                            <CardDiario
                                cor={item.cor}
                                icon={item.icon}
                                count={item.count}
                                title={item.title}
                            />
                        </div>

                    ))
                }
            </div>

            <h3 style={{ 'margin': '10px' }}>Contratos: Fevereiro</h3>
            <div className="row">

                {
                    dataContratos && dataContratos.map((item, index) => (
                        <div className="col-4" key={index}>
                            <StatusCard
                                cor={item.cor}
                                icon={item.icon}
                                count={item.count}
                                title={item.title}
                            />
                        </div>
                    ))
                }

            </div>
            <div className="titulo-filtro">
                <h3 style={{ 'margin': '10px' }}>Recebimentos:  {valueMes + '/' + valueAno}</h3>
                <div className='filto'>
                    {/* <Autocomplete
                        value={value}
                        onChange={(event, newValue) => {
                            setValue(newValue);
                        }}
                        inputValue={inputValue}
                        onInputChange={(event, newInputValue) => {
                            setInputValue(newInputValue);
                        }}
                        id="controllable-states-demo"
                        options={FiltroMes}
                        style={{ width: 300 }}
                        renderInput={(params) => <TextField {...params} label="Controllable" variant="outlined" />}
                    /> */}
                    <div className="input_container">
                        <input className='input_field' type="search" value={valueMes} placeholder="Mes" onChange={e => handleChangeMes(e.target.value)} />
                    
                        <input className='input_field' type="search" value={valueAno} placeholder="Ano" onChange={e => handleChangeAno(e.target.value)} />
                    </div>

                    <button className='botao-enviar'
                        onClick={() => handleClick()}
                    >
                        Enviar
                    </button>


                </div>
            </div>
            <div className="row">

                {
                    loading ? <CircularProgress color="secondary" /> : dataRecebimentos.map((item, index) => (
                        <div className="col-4" key={index}>
                            <StatusCard
                                cor={item.cor}
                                icon={item.icon}
                                count={item.count}
                                title={item.title}
                            />
                        </div>
                    ))
                }

            </div>

            <div className="col-12">
                <div className="card full-height">
                    <h3>Faturamento(Mensalidades)</h3>
                    <Chart
                        options={ThemeReducer === 'theme-mode-dark' ? {
                            ...chartOptions.options,
                            theme: { mode: 'dark' }
                        } : {
                            ...chartOptions.options,
                            theme: { mode: 'light' }
                        }}
                        series={chartOptions.series}
                        type='bar'
                        height='150%'
                        width='100%' >

                    </Chart>
                </div>
            </div>
            <div className="col-12">
                <div className="card full-height">
                    <h3>Faturamento taxa de instalação</h3>
                    <Chart
                        options={ThemeReducer === 'theme-mode-dark' ? {
                            ...chartOptions.options,
                            theme: { mode: 'dark' }
                        } : {
                            ...chartOptions.options,
                            theme: { mode: 'light' }
                        }}
                        series={chartOptions.series}
                        type='bar'
                        height='150%'
                        width='100%' >

                    </Chart>
                </div>
            </div>
            <div className="col-12">
                <div className="card full-height">
                    <h3>Instalações</h3>
                    <Chart
                        options={ThemeReducer === 'theme-mode-dark' ? {
                            ...chartOptions.options,
                            theme: { mode: 'dark' }
                        } : {
                            ...chartOptions.options,
                            theme: { mode: 'light' }
                        }}
                        series={chartOptions.series}
                        type='bar'
                        height='150%'
                        width='100%' >

                    </Chart>
                </div>
            </div>
        </div>
    )
}

export default Dashboard 