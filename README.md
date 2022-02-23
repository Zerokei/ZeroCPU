# ZeroCPU
The CPU designed by Zerokei :)

## How to use it

Generate CPU.v in build/verilog/base automatically

```shell
make
```


## Debug
Generate TieleForVerilator.v in build/verilog/base automatically
```shell
make debug
```
Verify the code with [ZJV2-difftest](https://github.com/riscv-zju/ZJV2-difftest)

![](https://s2.loli.net/2022/02/22/ZCpk63r7xMVS2Pu.png)

Moreover, we need the [environment](https://github.com/riscv-zju/zjv2-env)

Creat the container and mount directory

Then enter the container
```shell
cd ZJV2-difftest
bash run.sh
```
## Design
![](https://s2.loli.net/2022/02/23/jakuWtvDfHFZYcV.png)
![](https://s2.loli.net/2022/02/22/Em4OnDS9kfwxMoP.png)
