import os
import torch
import torch.nn as nn
import torch.nn.functional as F
import ai_edge_torch

import numpy
import torchvision


class SampleModel(nn.Module):
    def __init__(self):
        super().__init__()
        # self.conv1 = nn.Conv2d(1, 20, 5)
        # self.conv2 = nn.Conv2d(20, 20, 5)
        self.weights = nn.Parameter(torch.tensor([2.0]), requires_grad=True)
        self.weights_2 = nn.Parameter(torch.tensor([2.0]), requires_grad=True)

    def forward(self, x):
        # x = F.relu(self.conv1(x))
        # return F.relu(self.conv2(x))
        a = self.weights * x
        a_1 = a.reshape(1, -1)
        b = a_1.sum(dim=-1)
        # b = a_1.sum()
        c = b.reshape(-1) * self.weights_2
        return c


def export_model():
    os.environ["PJRT_DEVICE"] = "CPU"

    col_red = "\x1b[31m"
    col_green = "\x1b[32m"
    col_yellow = "\x1b[33m"
    col_reset = "\x1b[0m"

    print(f"{col_green}Creating Model{col_reset}")
    model = SampleModel().eval()
    input_tensor = torch.tensor([1.0, 2.0, 3.0])

    # model = torchvision.models.resnet18(
    #     torchvision.models.ResNet18_Weights.IMAGENET1K_V1
    # ).eval()
    # input_tensor = (torch.randn(1, 3, 224, 224),)

    expected_output = model(input_tensor)

    print(f"{col_green}Converting Model{col_reset}")
    edge_model = ai_edge_torch.convert(model.eval(), (input_tensor,))

    print(f"{col_green}Validating Model{col_reset}")
    model_output = edge_model(input_tensor)
    model_output = torch.from_numpy(model_output)

    if torch.isclose(expected_output, model_output, atol=1e-5).all():
        should_export_model = True
        print(f"{col_green}Model is valid{col_reset}")
    else:
        should_export_model = False
        print(f"{col_red}Model is invalid{col_reset}")
        print(f"{col_red}Expected output:{col_reset} {expected_output}")
        print(f"{col_red}Model    output:{col_reset} {model_output}")

    if should_export_model:
        print(f"{col_green}Exporting Model{col_reset}")
        edge_model.export("model.tflite")


if __name__ == "__main__":
    export_model()
