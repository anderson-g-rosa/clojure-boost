(ns clojure-boost.logic
  (:use clojure.pprint)
  (:require [clojure-boost.utils :as utils]))

(defn nova-compra
  "Retorna estrutura de dados de uma compra realizada"
  [data, valor, estabelecimento, categoria, cartao]
  {:data            data
   :valor           valor
   :estabelecimento estabelecimento
   :categoria       categoria
   :cartao          cartao})

;(println (nova-compra "2022-01-01", 129.90, "Outback", "Alimentação", 1234123412341234))


(defn lista-compras []
  "Retorna lista de compras a partir do csv"
  (let [arquivo "compras.csv"
        campos [:data, :valor, :estabelecimento, :categoria, :cartao]]
    (utils/ler-csv arquivo campos)))

;(pprint (lista-compras))


(defn lista-compras-formatada
  "Retorna lista de compras formatada com a data padrão dd/MM/yyyy e campo valor como bigDecimal"
  [lista-compras-original]
  (->> lista-compras-original
       (map #(utils/atualiza-formato-data % :data "yyyy-MM-dd" "dd/MM/yyyy"))
       (map #(update % :valor bigdec))))

;(pprint (lista-compras-formatada (lista-compras)))


(defn total-gasto
  "Retorna o total gasto a partir de uma lista de compras"
  [compras]
  (->>
    compras
    (map :valor)
    (reduce +)
    bigdec))

;(pprint (total-gasto (lista-compras)))


(defn compras-por-cartao
  "Retorna lista de compras a partir de um determinado número de cartão"
  [compras cartao]
  (filter #(= (:cartao %) cartao) compras))

;(pprint (compras-por-cartao (lista-compras) 3939393939393939))


(defn compras-por-mes
  "Retorna lista de compras a partir de um mês.
  Formato da data espero na lista de compras é yyyy-MM-dd "
  [mes compras]
  (let [formato-atual-da-data "yyyy-MM-dd"
        formato-mes-da-data "MM"]
    (->> compras
         (filter #(= (Integer/parseInt (utils/formata-data (:data %) formato-atual-da-data formato-mes-da-data)) mes)))))

;(pprint (compras-por-mes 3 (lista-compras)))
;(pprint (total-gasto (compras-por-cartao (compras-por-mes 03 (lista-compras)) 3939393939393939)))


(defn total-gasto-no-mes
  "Retorna o valor total de gasto em bigDecimal das compras de um determinado mês"
  [mes compras]
  (let [compras-do-mes (compras-por-mes mes compras)
        valores-da-compra (map :valor compras-do-mes)]
    (bigdec (reduce + valores-da-compra))))

;(pprint (total-gasto-no-mes 1 (lista-compras)))


(defn agrupa-categoria
  "Retorna uma lista com as compras agrupadas por categoria "
  [compras]
  (->> compras
       (group-by :categoria)))

(defn mapeia-compras-por-categoria
  "Retorna uma lista com vetores que contém o nome da categoria e total de compras da categoria"
  [[categoria compras]]
  [categoria
   (total-gasto compras)])

(defn total-compras-por-categoria
  "Retorna uma coleção com o nome da categoria e o valor total da categoria"
  [compras]
  (->> compras
       agrupa-categoria
       (map mapeia-compras-por-categoria)
       (into {})))

;(pprint (total-compras-por-categoria (lista-compras)))


(defn filtra-compras-por-valor
  "Retorna compras a partir de um valor máximo e valor minimo"
  [valor-maximo valor-minimo]
  (->> (lista-compras)
       (filter #(>= (:valor %) valor-minimo))
       (filter #(<= (:valor %) valor-maximo))))

;(pprint (filtra-compras-por-valor 200000 0))


(defn lista-cartoes []
  "Retorna lista de cartões a partir do csv"
  (let [arquivo "cartao.csv"
        campos [:numero, :cvv, :validade, :limite, :cliente]]
    (utils/ler-csv arquivo campos)))

(defn lista-cartao-formatada
  "Retorna lista de cartões formatada com a validade padrão dd/MM/yyyy e campo limite como bigDecimal"
  [lista-cartao-original]
  (->> lista-cartao-original
       (map #(utils/atualiza-formato-data % :validade "yyyy-MM" "MM/yyyy"))
       (map #(update % :limite bigdec))))

;(pprint (lista-cartao-formatada (lista-cartoes)))