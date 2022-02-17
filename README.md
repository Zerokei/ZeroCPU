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

![](https://gitee.com/zerokei/tu-chuang/raw/master/202202121525876.png)

Moreover, we need the [environment](https://github.com/riscv-zju/zjv2-env)

Creat the container and mount directory

Then enter the container
```shell
cd ZJV2-difftest
bash run.sh
```
## Design
![](https://gitee.com/zerokei/tu-chuang/raw/master/202202171948123.png)
